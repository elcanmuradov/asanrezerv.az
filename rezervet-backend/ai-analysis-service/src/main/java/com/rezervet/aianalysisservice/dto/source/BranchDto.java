package com.rezervet.aianalysisservice.dto.source;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchDto {
    private UUID id;
    private UUID restaurantId;
    private String name;
    private String workingHours;
}
