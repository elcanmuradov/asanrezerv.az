package com.rezervet.aianalysisservice.client;

import com.rezervet.aianalysisservice.dto.ApiResponse;
import com.rezervet.aianalysisservice.dto.source.BranchDto;
import com.rezervet.aianalysisservice.dto.source.RestaurantDto;
import com.rezervet.aianalysisservice.dto.source.TableDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "restaurant-service", url = "${spring.client.urls.restaurant-service}/api/restaurants")
public interface RestaurantClient {

    // Sahibin (X-User-Id) restoranı — restaurantId-ni server tərəfdə həll edir
    @GetMapping("/restaurant")
    ApiResponse<RestaurantDto> getMyRestaurant(@RequestHeader("X-User-Id") UUID userId);

    @GetMapping("/{id}/branches")
    ApiResponse<List<BranchDto>> getBranches(@PathVariable UUID id);

    @GetMapping("/branches/{branchId}/tables")
    ApiResponse<List<TableDto>> getTables(@RequestHeader("X-User-Id") UUID userId, @PathVariable UUID branchId);
}
