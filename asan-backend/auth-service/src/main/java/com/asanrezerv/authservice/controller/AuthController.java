package com.asanrezerv.authservice.controller;

import com.asanrezerv.authservice.dto.ApiResponse;
import com.asanrezerv.authservice.dto.UserDto;
import com.asanrezerv.authservice.dto.auth.request.*;
import com.asanrezerv.authservice.dto.auth.request.*;
import com.asanrezerv.authservice.dto.auth.response.AuthResponse;
import com.asanrezerv.authservice.dto.auth.response.VerifyOtpCodeResponse;
import com.asanrezerv.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register/initiate")
    public void initiate(@Valid @RequestBody InitiateRegister request) {
        authService.sendOtpCode(request);
    }


    @PostMapping("/register/verify")
    public ResponseEntity<ApiResponse<VerifyOtpCodeResponse>> verify(@Valid @RequestBody VerifyOtpCodeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.verify(request)));
    }

    @PostMapping("/register/finish")
    public ResponseEntity<ApiResponse<AuthResponse>> finish(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.finishRegistration(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(request)));
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public void logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.refresh(request)));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserDto>> getUser(@RequestHeader("Authorization") String token) {
       return ResponseEntity.ok(ApiResponse.success(authService.getUser(token)));
    }




}
