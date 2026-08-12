package com.asanrezerv.restaurantservice.dto.clients.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantQuotaUsage {
    private UUID id;

    private UUID restaurantId;

    private int currentBranchCount;
}
