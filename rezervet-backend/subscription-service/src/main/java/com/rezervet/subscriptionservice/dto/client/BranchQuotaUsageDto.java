package com.rezervet.subscriptionservice.dto.client;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BranchQuotaUsageDto {

    private UUID id;

    private UUID branchId;

    private int currentTableCount;

}
