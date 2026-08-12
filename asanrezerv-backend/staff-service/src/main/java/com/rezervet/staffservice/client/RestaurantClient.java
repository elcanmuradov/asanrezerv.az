package com.rezervet.staffservice.client;

import com.rezervet.staffservice.dto.ApiResponse;
import com.rezervet.staffservice.dto.restaurant.BranchDto;
import com.rezervet.staffservice.dto.restaurant.RestaurantDto;
import com.rezervet.staffservice.enums.TableStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Component
@FeignClient(name = "restaurant-service", url = "${spring.restaurant-service.url}/api/restaurants")
public interface RestaurantClient {

    // DİQQƏT: restaurant-service-də bu endpoint-in path-i "/tables/change-status"-dır (path
    // variable YOXDUR) — məhz status/tableId request param-larla verilir.
    @PatchMapping("/tables/change-status")
    void changeStatus(@RequestParam(name = "status") TableStatus status, @RequestParam(name = "tableId") UUID tableId);

    @GetMapping("/restaurant")
    ApiResponse<RestaurantDto> getMyRestaurant(@RequestHeader("X-User-Id") UUID userId);

    @GetMapping("/{id}/branches")
    ApiResponse<List<BranchDto>> getBranchesByRestaurant(@PathVariable("id") UUID restaurantId);

    @GetMapping("/branches/{id}")
    ApiResponse<BranchDto> getBranchById(@PathVariable("id") UUID id);
}
