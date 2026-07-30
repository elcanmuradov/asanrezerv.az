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
public class BranchQuotaUsage {
    private UUID id;

    private UUID branchId;

    private int currentTableCount;
}
