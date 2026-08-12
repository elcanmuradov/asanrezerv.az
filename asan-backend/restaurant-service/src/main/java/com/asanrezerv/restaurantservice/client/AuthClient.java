package com.asanrezerv.restaurantservice.client;

import com.asanrezerv.restaurantservice.dto.ApiResponse;
import com.asanrezerv.restaurantservice.dto.clients.auth.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "auth-service", url = "${spring.services.auth-service.url}")
public interface AuthClient {

    @GetMapping("/internal/users/{id}")
    ApiResponse<UserDto> getUserById(@PathVariable UUID id);
}
