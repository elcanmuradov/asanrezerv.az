package com.rezervet.staffservice.controller;

import com.rezervet.staffservice.dto.ApiResponse;
import com.rezervet.staffservice.dto.staff.StaffDto;
import com.rezervet.staffservice.dto.staff.StaffRequest;
import com.rezervet.staffservice.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager")
public class ManagerController {

    private final ManagerService managerService;





    // ---- İşçilər (ofisiantlar) ----
    @GetMapping("/staff")
    public ResponseEntity<ApiResponse<List<StaffDto>>> getStaff(
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(managerService.getStaff(userId)));
    }

    // Servisdə: auth-service-ə daxili çağırış (WAITER hesabı) + StaffAssignment yaz
    @PostMapping("/staff")
    public ResponseEntity<ApiResponse<StaffDto>> createWaiter(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody StaffRequest request) {
        return ResponseEntity.ok(ApiResponse.success(managerService.createWaiter(userId, request)));
    }

    @DeleteMapping("/staff/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWaiter(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id) {
        managerService.deleteWaiter(userId, id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }


//    // ---- Analitika (rezerv sahələri reservation-service olana qədər 0/boş) ----
//    @GetMapping("/analytics/summary")
//    public ResponseEntity<ApiResponse<AnalyticsSummaryDto>> analyticsSummary(
//            @RequestHeader("X-User-Id") UUID userId) {
//        return ResponseEntity.ok(ApiResponse.success(managerService.getAnalyticsSummary(userId)));
//    }
//
//    @GetMapping("/analytics/reservations-monthly")
//    public ResponseEntity<ApiResponse<List<MonthlyStatDto>>> reservationsMonthly(
//            @RequestHeader("X-User-Id") UUID userId) {
//        return ResponseEntity.ok(ApiResponse.success(managerService.getMonthlyReservationStats(userId)));
//    }
}