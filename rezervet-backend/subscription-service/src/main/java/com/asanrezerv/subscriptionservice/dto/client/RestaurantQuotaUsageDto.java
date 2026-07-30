package com.asanrezerv.subscriptionservice.dto.client;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RestaurantQuotaUsageDto {
    private UUID id;

    private UUID restaurantId;

    private int currentBranchCount;

}
