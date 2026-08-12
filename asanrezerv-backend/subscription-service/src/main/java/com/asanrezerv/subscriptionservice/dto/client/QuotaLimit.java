package com.asanrezerv.subscriptionservice.dto.client;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuotaLimit {
    private int maxBranches;

    private int maxTablesPerBranch;
}
