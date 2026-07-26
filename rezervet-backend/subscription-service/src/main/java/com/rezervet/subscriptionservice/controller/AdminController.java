package com.rezervet.subscriptionservice.controller;

import com.rezervet.subscriptionservice.dto.ApiResponse;
import com.rezervet.subscriptionservice.dto.controller.PlanDto;
import com.rezervet.subscriptionservice.dto.controller.PlanRequest;
import com.rezervet.subscriptionservice.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<PlanDto>>> getPlans() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getPlans()));
    }

    @PutMapping("/plans/{id}")
    public ResponseEntity<ApiResponse<PlanDto>> updatePlan(@PathVariable UUID id, @RequestBody PlanRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminService.updatePlan(id,request)));
    }

    @PostMapping("/plans")
    public ResponseEntity<ApiResponse<PlanDto>> createPlan(@RequestBody PlanRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminService.create(request)));
    }

}


/*

GET /api/admin/plans → List<PlanDto> (bütün sahələr: id, name, monthlyPrice, maxBranches, maxTablesPerBranch, visibilityLevel, description, features[], popular)
PUT /api/admin/plans/{id} → body eyni sahələr; yenilənmiş PlanDto qaytarır.
 */