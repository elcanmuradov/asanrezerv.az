package com.rezervet.subscriptionservice.dto.client;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuotaLimit {
    private int max_branches;

    private int max_tables_per_branch;
}
