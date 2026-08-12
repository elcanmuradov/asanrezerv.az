package com.asanrezerv.restaurantservice.dto.kafka;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TableCreationEvent {

    private UUID restaurantId;

    private UUID branchId;

}
