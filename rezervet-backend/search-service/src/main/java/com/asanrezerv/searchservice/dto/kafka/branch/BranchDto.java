package com.asanrezerv.searchservice.dto.kafka.branch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

// restaurant-service-in "branch.created" / "branch.updated" topic-lərinə göndərdiyi
// BranchEventDto ilə eyni sahə adları (field-name əsaslı Jackson bind).
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchDto {
    private UUID restaurantId;
    private UUID branchId;

    private String name;
    private String city;
    private String district;
    private String address;
    private Double latitude;
    private Double longitude;

    private Integer minTableCapacity;
    private Integer maxTableCapacity;

    private String openingTime;
    private String closingTime;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
