package com.rezervet.staffservice.client;

import com.rezervet.staffservice.dto.ApiResponse;
import com.rezervet.staffservice.dto.staff.StaffRequest;
import com.rezervet.staffservice.dto.staff.WaiterAuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(name = "auth-service",url = "${spring.auth-service.url}")
public interface AuthClient {

    @PostMapping("/internal/create-waiter")
    ApiResponse<WaiterAuthResponse> createWaiter(@RequestBody StaffRequest request);

}
