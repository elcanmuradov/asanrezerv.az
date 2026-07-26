package com.rezervet.authservice.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @NotBlank
    private String refreshToken;
}