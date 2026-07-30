package com.asanrezerv.subscriptionservice.dto.controller;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PlanDto {
    private UUID id;

    private String name;

    private String description;

    private boolean mostPopular;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> features;

    private Integer maxBranches;

    private Integer maxTablesPerBranch;

    private Integer visibilityLevel;

    private Integer aiAnalyticLevel;

    private BigDecimal monthlyPrice;
}
