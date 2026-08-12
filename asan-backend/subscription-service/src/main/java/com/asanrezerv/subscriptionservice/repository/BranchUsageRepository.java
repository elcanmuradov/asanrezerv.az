package com.asanrezerv.subscriptionservice.repository;

import com.asanrezerv.subscriptionservice.entity.BranchQuotaUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BranchUsageRepository extends JpaRepository<BranchQuotaUsage, UUID> {

    Optional<BranchQuotaUsage> findBranchQuotaUsageByBranchId(UUID branchId);
    
}
