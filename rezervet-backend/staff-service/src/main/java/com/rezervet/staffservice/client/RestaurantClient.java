package com.rezervet.staffservice.client;

import com.rezervet.staffservice.enums.TableStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Component
@FeignClient(name = "restaurant-service", url = "${spring.restaurant-service.url}/api/restaurants")
public interface RestaurantClient {
    @PatchMapping("/tables/{id}/change-status")
    void changeStatus(@RequestParam(name = "status") TableStatus status, @RequestParam(name = "tableId") UUID id);
}
