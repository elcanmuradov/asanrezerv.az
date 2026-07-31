package com.rezervet.reservationservice.dto.restaurant;

import lombok.Data;

import java.util.UUID;

// restaurant-service-in BranchDto-su ilə eyni sahə adları (field-name əsaslı Jackson bind).
@Data
public class BranchDto {
    private UUID id;
    private UUID restaurantId;
    private String name;
    private String openingTime;
    private String closingTime;
}
