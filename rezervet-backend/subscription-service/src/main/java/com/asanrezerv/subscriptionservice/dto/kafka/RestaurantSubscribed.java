package com.asanrezerv.subscriptionservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantSubscribed {

    private UUID restaurantId;

    private Integer visibilityLevel;

    private Integer aiAnalysisLevel;


}
