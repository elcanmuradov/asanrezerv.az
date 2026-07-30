package com.asanrezerv.subscriptionservice.dto.controller;

import com.asanrezerv.subscriptionservice.enums.SubscriptionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class SubscriptionDto {
    private UUID id;

    private UUID restaurantId;

    private UUID planId;

    private LocalDate startDate;

    private LocalDate endDate;

    private SubscriptionStatus status;
}
