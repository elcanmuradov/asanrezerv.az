package com.rezervet.restaurantservice.dto.clients.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotaLimits {

    private int max_branches;

    private int max_tables_per_branch;

}
