package com.asanrezerv.subscriptionservice.dto.client;

import lombok.Data;

import java.util.UUID;

// restaurant-service-in RestaurantDto-su ilə eyni sahə adları (field-name əsaslı Jackson bind).
@Data
public class RestaurantDto {
    private UUID id;
    private String name;
}
