package com.asanrezerv.restaurantservice.controller;

import com.asanrezerv.restaurantservice.dto.ApiResponse;
import com.asanrezerv.restaurantservice.dto.restaurant.AdminRestaurantDto;
import com.asanrezerv.restaurantservice.enums.RestaurantStatus;
import com.asanrezerv.restaurantservice.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/restaurants")
public class AdminController {

    private final RestaurantService restaurantService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminRestaurantDto>>> getRestaurants() {
        return ResponseEntity.ok(ApiResponse.success(restaurantService.getAdminRestaurants()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        restaurantService.updateRestaurantStatus(id, RestaurantStatus.valueOf(body.get("status")));
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
