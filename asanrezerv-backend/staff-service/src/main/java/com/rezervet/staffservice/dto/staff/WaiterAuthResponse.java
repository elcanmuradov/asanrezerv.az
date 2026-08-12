package com.rezervet.staffservice.dto.staff;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class WaiterAuthResponse {
    private UUID id;

    private String email;

    private String fullName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
