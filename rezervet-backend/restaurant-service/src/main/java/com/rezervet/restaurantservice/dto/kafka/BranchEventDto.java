package com.rezervet.restaurantservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * "branch.created" və "branch.updated" topic-lərinə göndərilən tam filial datası.
 * DİQQƏT: `restaurantId` və `branchId` sahə adları qəsdən subscription-service-in
 * mövcud BranchCreationEvent-i ilə eynidir (o, əlavə sahələri sadəcə iqnor edir) —
 * beləliklə bu event həm search-service-i, həm subscription-service-in mövcud
 * "branch.created" consumer-ini eyni vaxtda, dəyişiklik etmədən təmin edir.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchEventDto {
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

    private String openingTime; // "HH:mm:ss"
    private String closingTime;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
