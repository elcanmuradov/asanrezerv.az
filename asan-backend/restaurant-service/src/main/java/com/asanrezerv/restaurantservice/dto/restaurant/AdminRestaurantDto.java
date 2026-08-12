package com.asanrezerv.restaurantservice.dto.restaurant;

import com.asanrezerv.restaurantservice.enums.RestaurantStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

// Admin > Restoranlar siyahısı üçün (owner/plan/filial sayı zənginləşdirilib)
@Data
@Builder
public class AdminRestaurantDto {
    private UUID id;
    private String name;
    private String ownerName;
    private String ownerEmail;
    private String planName;
    private int branchCount;
    private RestaurantStatus status;
}
