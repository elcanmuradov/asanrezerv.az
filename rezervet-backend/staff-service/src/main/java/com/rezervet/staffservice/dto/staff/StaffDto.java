package com.rezervet.staffservice.dto.staff;

import com.rezervet.staffservice.enums.StaffRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StaffDto {
    private UUID id;

    private UUID userId;

    private UUID branchId;

    private StaffRole role;

    private LocalDateTime createdAt;
}
