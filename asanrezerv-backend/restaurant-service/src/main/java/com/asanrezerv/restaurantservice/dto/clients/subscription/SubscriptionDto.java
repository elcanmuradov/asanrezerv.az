package com.asanrezerv.restaurantservice.dto.clients.subscription;

import lombok.Data;

import java.util.UUID;

// subscription-service-in SubscriptionDto-su ilə eyni sahə adları (field-name əsaslı Jackson bind).
@Data
public class SubscriptionDto {
    private UUID id;
    private UUID restaurantId;
    private UUID planId;
    private String planName;
}
