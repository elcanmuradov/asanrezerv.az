package com.rezervet.aianalysisservice.dto.source;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

// restaurant-service RestaurantDto-nun lokal kopyası (yalnız id lazımdır)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDto {
    private UUID id;
    private UUID ownerId;
    private String name;
    private String city;
}
