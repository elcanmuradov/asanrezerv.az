package com.asanrezerv.searchservice.dto.kafka.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantSubscribed {

    private UUID restaurantId;

    private Integer visibilityLevel;

    private Integer aiAnalysisLevel;


}
