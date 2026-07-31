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

    private String fullName;

    private String email;

    private BranchSummary branch;

    private StaffRole role;

    private LocalDateTime createdAt;

    @Data
    @Builder
    public static class BranchSummary {
        private UUID id;
        private String name;
    }
}
