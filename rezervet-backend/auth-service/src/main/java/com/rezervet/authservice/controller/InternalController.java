package com.rezervet.authservice.controller;

import com.rezervet.authservice.dto.ApiResponse;
import com.rezervet.authservice.dto.UserDto;
import com.rezervet.authservice.dto.client.StaffRequest;
import com.rezervet.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

@RequestMapping("/internal")
public class InternalController {

    private final AuthService authService;

    @PostMapping("/create-waiter")
    public ResponseEntity<ApiResponse<UserDto>> createWaiter(@RequestBody StaffRequest staffRequest) {
        return ResponseEntity.ok(ApiResponse.success(authService.createWaiter(staffRequest)));
    }
}
