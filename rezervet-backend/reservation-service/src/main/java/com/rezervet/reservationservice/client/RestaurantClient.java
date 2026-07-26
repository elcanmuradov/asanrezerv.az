package com.rezervet.reservationservice.client;

import com.rezervet.reservationservice.dto.ApiResponse;
import com.rezervet.reservationservice.dto.restaurant.TableDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "restaurant-service",url = "${spring.client.urls.restaurant-service}/api/restaurants")
public interface RestaurantClient {
    @GetMapping("/branches/{branchId}/tables")
    ApiResponse<List<TableDto>>  getTablesByBranchId(@RequestHeader("X-User-Id") UUID userId, @PathVariable UUID branchId);

}
