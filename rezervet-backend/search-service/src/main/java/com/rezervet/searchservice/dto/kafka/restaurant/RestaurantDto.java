package com.rezervet.searchservice.dto.kafka.restaurant;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RestaurantDto {
    private UUID id;

    private UUID ownerId;

    private String name;

    private String cuisine;

    private String city;

    private String description;

    private String phone;

    private MediaAssetsDto mediaAssets;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
