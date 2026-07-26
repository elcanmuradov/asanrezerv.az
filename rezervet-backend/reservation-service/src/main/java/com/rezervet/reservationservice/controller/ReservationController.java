package com.rezervet.reservationservice.controller;

import com.rezervet.reservationservice.dto.ApiResponse;
import com.rezervet.reservationservice.dto.ReservationDto;
import com.rezervet.reservationservice.dto.request.ReservationRequest;
import com.rezervet.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping("/restaurant/{id}")
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getReservations(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.getReservations(id)));
    }

    @GetMapping("/branch/{id}")
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getReservationsByBranchId(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.getReservationsByBranchId(id)));
    }

    @PostMapping("/branch/availability")
    public ResponseEntity<ApiResponse<UUID>> checkAvailability(@RequestHeader("X-User-Id") UUID userId ,@RequestBody ReservationRequest reservationRequest) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.checkAvailability(userId,reservationRequest)));
    }

    @PostMapping("/branch/reserve")
    public ResponseEntity<ApiResponse<ReservationDto>> reserveTable(@RequestHeader("X-User-Id") UUID userId,@RequestBody ReservationRequest reservationRequest) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.reserveTable(userId,reservationRequest)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getMyReservations(@RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.getUserReservations(userId)));
    }


}
