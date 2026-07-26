package com.rezervet.searchservice.dto.kafka.subscription;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RestaurantSubscribed {

    private UUID restaurantId;

    private Integer visibilityLevel;

    private Integer aiAnalysisLevel;


}
