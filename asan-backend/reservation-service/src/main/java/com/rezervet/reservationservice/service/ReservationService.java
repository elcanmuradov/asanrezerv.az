package com.rezervet.reservationservice.service;

import com.rezervet.reservationservice.client.RestaurantClient;
import com.rezervet.reservationservice.dto.ReservationDto;
import com.rezervet.reservationservice.dto.request.ReservationRequest;
import com.rezervet.reservationservice.dto.restaurant.BranchDto;
import com.rezervet.reservationservice.dto.restaurant.TableDto;
import com.rezervet.reservationservice.entity.Reservation;
import com.rezervet.reservationservice.enums.ReservationStatus;
import com.rezervet.reservationservice.enums.Source;
import com.rezervet.reservationservice.exception.NotFoundException;
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
import java.time.YearMonth;
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
                .guestPhone(r.getGuestPhone())
                .guestCount(r.getGuestCount())
                .note(r.getNote())
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

    // Menecer/ofisiantın əl ilə (zəng, gəlmə) daxil etdiyi rezerv — hesabı olmayan qonaq üçün.
    public ReservationDto createManualReservation(UUID staffUserId, ReservationRequest r) {
        int duration = r.getDuration() != null ? r.getDuration() : 90;
        validateWithinBranchHours(r.getBranchId(), r.getStartTime(), r.getStartTime().plusMinutes(duration));

        UUID tableId = r.getTableId() != null ? r.getTableId() : checkAvailability(staffUserId, r);

        Reservation reservation = Reservation.builder()
                .guestName(r.getGuestName())
                .guestPhone(r.getGuestPhone())
                .guestCount(r.getGuestCount())
                .note(r.getNote())
                .guestId(null)
                .restaurantId(r.getRestaurantId())
                .branchId(r.getBranchId())
                .tableId(tableId)
                .date(r.getDate())
                .startTime(r.getStartTime())
                .endTime(r.getStartTime().plusMinutes(r.getDuration() != null ? r.getDuration() : 90))
                .status(ReservationStatus.CONFIRMED)
                .source(Source.MANUAL)
                .build();

        reservation = reservationRepository.save(reservation);

        return toReservationDto(reservation);
    }

    public long countThisMonth() {
        YearMonth ym = YearMonth.now();
        return reservationRepository.countByDateBetween(ym.atDay(1), ym.atEndOfMonth());
    }

    public ReservationDto updateStatus(UUID id, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rezerv tapılmadı!"));
        reservation.setStatus(status);
        reservation = reservationRepository.save(reservation);
        return toReservationDto(reservation);
    }

    public UUID checkAvailability(UUID userId,ReservationRequest request) {
        int duration = request.getDuration() != null ? request.getDuration() : 90;
        validateWithinBranchHours(request.getBranchId(), request.getStartTime(), request.getStartTime().plusMinutes(duration));

        List<TableDto> tables = fetchTables(userId, request.getBranchId());

        List<TableDto> suitableTables = tables.stream()
                .filter(table -> (table.getCapacity() >= request.getGuestCount()))
                .toList();

        LocalTime startTime = request.getStartTime();
        LocalTime endTime = request.getStartTime().plusMinutes(request.getDuration() != null ? request.getDuration() : 90);

        var availableTables =  getAvailableTableIds(suitableTables, request.getBranchId(), request.getDate(), startTime, endTime);
        if (availableTables.isEmpty()) {
            throw new ReservationException("Uyğun masa tapılmadı!");
        }
        return availableTables.getFirst();
    }

    public List<ReservationDto> getReservations(UUID userId, UUID restaurantId) {
        List<Reservation> reservations = reservationRepository.findReservationsByRestaurantId(restaurantId);

        Map<UUID, String> branchNames = restaurantClient.getBranchesByRestaurant(restaurantId).getData().stream()
                .collect(Collectors.toMap(BranchDto::getId, BranchDto::getName));

        Map<UUID, String> tableNames = new HashMap<>();
        for (UUID branchId : reservations.stream().map(Reservation::getBranchId).collect(Collectors.toSet())) {
            fetchTables(userId, branchId).forEach(t -> tableNames.put(t.getId(), t.getName()));
        }

        return reservations.stream().map(r -> toReservationDto(r, branchNames, tableNames)).toList();
    }

    public List<ReservationDto> getReservationsByBranchId(UUID userId, UUID branchId) {
        List<Reservation> reservations = reservationRepository.findReservationsByBranchId(branchId);

        String branchName = restaurantClient.getBranchById(branchId).getData().getName();
        Map<UUID, String> branchNames = Map.of(branchId, branchName);

        Map<UUID, String> tableNames = fetchTables(userId, branchId).stream()
                .collect(Collectors.toMap(TableDto::getId, TableDto::getName));

        return reservations.stream().map(r -> toReservationDto(r, branchNames, tableNames)).toList();
    }

    // Rezerv saatı filialın açılış/bağlanış saatlarından kənarda olmasın (front-end min/max
    // yalnız UX-dir — həqiqi qapı burdadır).
    private void validateWithinBranchHours(UUID branchId, LocalTime startTime, LocalTime endTime) {
        BranchDto branch = restaurantClient.getBranchById(branchId).getData();
        if (branch == null || branch.getOpeningTime() == null || branch.getClosingTime() == null) {
            return;
        }
        LocalTime open = LocalTime.parse(branch.getOpeningTime());
        LocalTime close = LocalTime.parse(branch.getClosingTime());
        if (startTime.isBefore(open) || endTime.isAfter(close)) {
            throw new ReservationException(
                    "Seçilmiş saat filialın iş saatlarına uyğun deyil (" + branch.getOpeningTime() + "–" + branch.getClosingTime() + ").");
        }
    }

    private List<TableDto> fetchTables(UUID userId, UUID branchId) {
        String json = stringRedisTemplate.opsForValue().get("branch:tables_" + branchId);
        if (Optional.ofNullable(json).isEmpty()) {
            List<TableDto> tables = restaurantClient.getTablesByBranchId(userId, branchId).getData();
            stringRedisTemplate.opsForValue().set("branch:tables_" + branchId, objectMapper.writeValueAsString(tables));
            return tables;
        }
        return objectMapper.readValue(json, new TypeReference<ArrayList<TableDto>>(){});
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
        return toReservationDto(reservation, Map.of(), Map.of());
    }

    private ReservationDto toReservationDto(Reservation reservation, Map<UUID, String> branchNames, Map<UUID, String> tableNames) {
        return ReservationDto.builder()
                .id(reservation.getId())
                .guestName(reservation.getGuestName())
                .guestPhone(reservation.getGuestPhone())
                .guestCount(reservation.getGuestCount())
                .note(reservation.getNote())
                .guestId(reservation.getGuestId())
                .restaurantId(reservation.getRestaurantId())
                .branchId(reservation.getBranchId())
                .branchName(branchNames.get(reservation.getBranchId()))
                .tableId(reservation.getTableId())
                .tableName(tableNames.get(reservation.getTableId()))
                .date(reservation.getDate())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
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