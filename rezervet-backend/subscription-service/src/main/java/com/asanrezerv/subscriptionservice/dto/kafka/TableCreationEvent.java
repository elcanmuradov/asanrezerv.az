package com.asanrezerv.subscriptionservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableCreationEvent {

    private UUID restaurantId;

    private UUID branchId;

}
