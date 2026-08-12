package com.asanrezerv.authservice.controller;

import com.asanrezerv.authservice.dto.ApiResponse;
import com.asanrezerv.authservice.dto.UserDto;
import com.asanrezerv.authservice.dto.client.StaffRequest;
import com.asanrezerv.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

// Digər mikroservislərin (gateway-dən keçmədən, Docker daxili şəbəkədən) çağırdığı marşrutlar.
@RestController
@RequiredArgsConstructor

@RequestMapping("/internal")
public class InternalController {

    private final AuthService authService;

    @PostMapping("/create-waiter")
    public ResponseEntity<ApiResponse<UserDto>> createWaiter(@RequestBody StaffRequest staffRequest) {
        return ResponseEntity.ok(ApiResponse.success(authService.createWaiter(staffRequest)));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(authService.getUserById(id)));
    }

    @GetMapping("/users/count")
    public ResponseEntity<ApiResponse<Long>> countUsers() {
        return ResponseEntity.ok(ApiResponse.success(authService.countUsers()));
    }
}
