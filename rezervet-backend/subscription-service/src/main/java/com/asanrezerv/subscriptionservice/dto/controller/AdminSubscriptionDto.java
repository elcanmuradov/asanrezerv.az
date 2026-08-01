package com.asanrezerv.subscriptionservice.dto.controller;

import com.asanrezerv.subscriptionservice.enums.SubscriptionStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

// Admin > Abunələr siyahısı üçün (restaurantName/planName zənginləşdirilib)
@Data
@Builder
public class AdminSubscriptionDto {
    private UUID id;
    private String restaurantName;
    private String planName;
    private LocalDate startedAt;
    private LocalDate currentPeriodEnd;
    private BigDecimal monthlyPrice;
    private SubscriptionStatus status;
}
