package com.asanrezerv.restaurantservice.dto.restaurant;

import com.asanrezerv.restaurantservice.enums.PublicationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RestaurantDto {
    private UUID id;

    private UUID ownerId;

    private String name;

    private String cuisine;

    private String city;

    private String description;

    private String phone;

    private MediaAssetsDto mediaAssets;

    private PublicationStatus publicationStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
