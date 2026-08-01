package com.asanrezerv.subscriptionservice.client;

import com.asanrezerv.subscriptionservice.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "auth-service", url = "${spring.client.urls.auth-service}")
public interface AuthClient {

    @GetMapping("/internal/users/count")
    ApiResponse<Long> countUsers();
}
