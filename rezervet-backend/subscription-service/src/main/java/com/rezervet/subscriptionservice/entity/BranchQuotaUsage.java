package com.rezervet.subscriptionservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BranchQuotaUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID branchId;

    private UUID restaurantId;

    private int currentTableCount;

}
