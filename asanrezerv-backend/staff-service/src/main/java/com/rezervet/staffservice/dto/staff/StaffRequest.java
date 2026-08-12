package com.rezervet.staffservice.dto.staff;

import lombok.Data;

import java.util.UUID;

@Data
public class StaffRequest {
    private String fullName;
    private String email;
    private UUID branchId;
}
