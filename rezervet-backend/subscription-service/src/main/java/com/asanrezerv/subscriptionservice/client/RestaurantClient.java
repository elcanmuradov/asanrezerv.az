package com.asanrezerv.subscriptionservice.client;

import com.asanrezerv.subscriptionservice.dto.ApiResponse;
import com.asanrezerv.subscriptionservice.dto.client.RestaurantDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "restaurant-service", url = "${spring.client.urls.restaurant-service}/api/restaurants")
public interface RestaurantClient {

    @GetMapping("/{id}")
    ApiResponse<RestaurantDto> getRestaurantById(@PathVariable UUID id);

    @GetMapping("/count")
    ApiResponse<Long> countRestaurants();
}
