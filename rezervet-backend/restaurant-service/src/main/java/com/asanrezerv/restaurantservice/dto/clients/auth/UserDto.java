package com.asanrezerv.restaurantservice.dto.clients.auth;

import lombok.Data;

import java.util.UUID;

// auth-service-in UserDto-su ilə eyni sahə adları (field-name əsaslı Jackson bind).
@Data
public class UserDto {
    private UUID id;
    private String email;
    private String fullName;
}
