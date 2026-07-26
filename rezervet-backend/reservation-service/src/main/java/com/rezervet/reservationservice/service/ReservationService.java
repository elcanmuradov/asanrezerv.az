package com.rezervet.reservationservice.service;

import com.rezervet.reservationservice.client.RestaurantClient;
import com.rezervet.reservationservice.dto.ReservationDto;
import com.rezervet.reservationservice.dto.request.ReservationRequest;
import com.rezervet.reservationservice.dto.restaurant.TableDto;
import com.rezervet.reservationservice.entity.Reservation;
import com.rezervet.reservationservice.enums.ReservationStatus;
import com.rezervet.reservationservice.enums.Source;
import com.rezervet.reservationservice.exception.ReservationException;
import com.rezervet.reservationservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ReservationRepository reservationRepository;
    private final ObjectMapper objectMapper;
    private final RestaurantClient restaurantClient;

    public ReservationDto reserveTable(UUID userId, ReservationRequest r) {
        UUID tableId = checkAvailability(userId,r);

        Reservation reservation = Reservation.builder()
                .guestName(r.getGuestName())
                .guestCount(r.getGuestCount())
                .guestId(userId)
                .restaurantId(r.getRestaurantId())
                .branchId(r.getBranchId())
                .tableId(tableId)
                .date(r.getDate())
                .startTime(r.getStartTime())
                .endTime(r.getStartTime().plusMinutes(r.getDuration()))
                .status(ReservationStatus.PENDING)
                .source(Source.ONLINE)
                .build();

        reservation = reservationRepository.save(reservation);

        return  toReservationDto(reservation);

    }

    public UUID checkAvailability(UUID userId,ReservationRequest request) {
        String json = stringRedisTemplate.opsForValue().get("branch:tables_" + request.getBranchId());
        List<TableDto> tables;
        if (Optional.ofNullable(json).isEmpty()) {
            tables = restaurantClient.getTablesByBranchId(userId,request.getBranchId()).getData();
            json = objectMapper.writeValueAsString(tables);
            stringRedisTemplate.opsForValue().set("branch:tables_" + request.getBranchId(), json);
        } else {
            tables =  objectMapper.readValue(json,new TypeReference<ArrayList<TableDto>>(){});
        }

        List<TableDto> suitableTables = tables.stream()
                .filter(table -> (table.getCapacity() >= request.getGuestCount()))
                .toList();

        LocalTime startTime = request.getStartTime();
        LocalTime endTime = request.getStartTime().plusMinutes(request.getGuestCount());

        var availableTables =  getAvailableTableIds(suitableTables, request.getBranchId(), request.getDate(), startTime, endTime);
        if (availableTables.isEmpty()) {
            throw new ReservationException("Uyğun masa tapılmadı!");
        }
        return availableTables.getFirst();
    }

    public List<ReservationDto> getReservations(UUID id) {
        List<ReservationDto> reservations = new ArrayList<>();
        reservationRepository.findReservationsByRestaurantId(id).forEach(reservation -> {
            reservations.add(toReservationDto(reservation));
        });

        return reservations;
    }

    public List<ReservationDto> getReservationsByBranchId(UUID branchId) {
        List<ReservationDto> reservations = new ArrayList<>();
        reservationRepository.findReservationsByBranchId(branchId).forEach(reservation -> {
            reservations.add(toReservationDto(reservation));
        });
        return reservations;
    }

    public List<ReservationDto> getUserReservations(UUID userId) {
        List<ReservationDto> reservations = new ArrayList<>();

        reservationRepository.findReservationsByGuestId(userId).forEach(reservation -> {
            reservations.add(toReservationDto(reservation));
        });

        return reservations;
    }


    private List<UUID> getAvailableTableIds(List<TableDto> tables, UUID branchId, LocalDate date, LocalTime startTime, LocalTime endTime) {


        List<Reservation> overlappingReservations = reservationRepository.findReservations(branchId, date, startTime, endTime);


        Set<UUID> reservedTableIds = overlappingReservations.stream()
                .map(Reservation::getTableId)
                .collect(Collectors.toSet());

        return tables.stream()

                .filter(table -> !reservedTableIds.contains(table.getId()))
                .sorted(Comparator.comparingInt(TableDto::getCapacity))
                .map(TableDto::getId)
                .toList();
    }




    private ReservationDto toReservationDto(Reservation reservation) {
        return ReservationDto.builder()
                .id(reservation.getId())
                .guestName(reservation.getGuestName())
                .guestCount(reservation.getGuestCount())
                .guestId(reservation.getGuestId())
                .restaurantId(reservation.getRestaurantId())
                .branchId(reservation.getBranchId())
                .tableId(reservation.getTableId())
                .date(reservation.getDate())
                .startTime(reservation.getStartTime())
                .status(reservation.getStatus())
                .source(reservation.getSource())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }



}
/*

    count check ->

    rezervasiya olunmamis masalarin siyahisi

    table.compare


 */