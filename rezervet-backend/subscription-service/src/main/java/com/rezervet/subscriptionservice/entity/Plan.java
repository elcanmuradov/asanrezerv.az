package com.rezervet.subscriptionservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String description;

    private boolean mostPopular;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> features;

    private Integer maxBranches;

    private Integer maxTablesPerBranch;

    private Integer visibilityLevel;

    private Integer aiAnalysisLevel;

    private BigDecimal monthlyPrice;


}
