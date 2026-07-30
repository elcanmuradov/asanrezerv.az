package com.asanrezerv.restaurantservice.dto.clients.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotaLimits {

    private int maxBranches;

    private int maxTablesPerBranch;

}
