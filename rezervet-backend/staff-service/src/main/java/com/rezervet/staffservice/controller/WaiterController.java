package com.rezervet.staffservice.controller;

import com.rezervet.staffservice.dto.ApiResponse;
import com.rezervet.staffservice.dto.restaurant.BranchDto;
import com.rezervet.staffservice.enums.TableStatus;
import com.rezervet.staffservice.service.WaiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/waiter")
public class WaiterController {

    private final WaiterService waiterService;

    @GetMapping("/branch")
    public ResponseEntity<ApiResponse<BranchDto>> getMyBranchId(
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(waiterService.getMyBranches(userId)));
    }



    @PatchMapping("/tables/{tableId}/status")
    public void updateStatus(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID tableId,
            @RequestBody TableStatus status) {
         waiterService.changeTableStatus(userId, tableId, status);
    }
}