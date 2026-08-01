package com.asanrezerv.subscriptionservice.dto.controller;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

// Admin > Platforma statistikası
@Data
@Builder
public class AdminStatsDto {
    private long restaurantCount;
    private long activeSubscriptions;
    private long userCount;
    private long monthlyReservations;
    private BigDecimal monthlyRevenue;
}
