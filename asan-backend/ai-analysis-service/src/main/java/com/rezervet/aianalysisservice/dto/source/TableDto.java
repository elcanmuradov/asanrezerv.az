package com.rezervet.aianalysisservice.dto.source;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableDto {
    private UUID id;
    private UUID branchId;
    private String name;
    private int capacity;
    private String zone;
}
