package com.asanrezerv.subscriptionservice.dto.controller;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
public class PlanRequest {
    private String name;

    private String description;

    private boolean mostPopular;

    private List<String> features;

    private Integer maxBranches;

    private Integer maxTablesPerBranch;

    private Integer visibilityLevel;

    private Integer aiAnalysisLevel;

    private BigDecimal monthlyPrice;
}
