package com.rezervet.reservationservice.controller;

import com.rezervet.reservationservice.dto.ApiResponse;
import com.rezervet.reservationservice.dto.ReservationDto;
import com.rezervet.reservationservice.dto.request.ReservationRequest;
import com.rezervet.reservationservice.enums.ReservationStatus;
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
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getReservations(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.getReservations(userId, id)));
    }

    @GetMapping("/branch/{id}")
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getReservationsByBranchId(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.getReservationsByBranchId(userId, id)));
    }

    @PostMapping("/branch/availability")
    public ResponseEntity<ApiResponse<UUID>> checkAvailability(@RequestHeader("X-User-Id") UUID userId ,@RequestBody ReservationRequest reservationRequest) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.checkAvailability(userId,reservationRequest)));
    }

    @PostMapping("/branch/reserve")
    public ResponseEntity<ApiResponse<ReservationDto>> reserveTable(@RequestHeader("X-User-Id") UUID userId,@RequestBody ReservationRequest reservationRequest) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.reserveTable(userId,reservationRequest)));
    }

    // Menecer/ofisiantın əl ilə (zəng, gəlmə) daxil etdiyi rezerv
    @PostMapping("/manual")
    public ResponseEntity<ApiResponse<ReservationDto>> createManualReservation(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody ReservationRequest reservationRequest) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.createManualReservation(userId, reservationRequest)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ReservationDto>> updateStatus(
            @PathVariable UUID id,
            @RequestParam ReservationStatus status) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.updateStatus(id, status)));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ReservationDto>> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.updateStatus(id, ReservationStatus.CANCELLED)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getMyReservations(@RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.getUserReservations(userId)));
    }

    // Platforma-geniş statistika üçün (admin/api/admin/stats) — Docker daxili şəbəkədən çağırılır.
    @GetMapping("/count/monthly")
    public ResponseEntity<ApiResponse<Long>> countThisMonth() {
        return ResponseEntity.ok(ApiResponse.success(reservationService.countThisMonth()));
    }

}
