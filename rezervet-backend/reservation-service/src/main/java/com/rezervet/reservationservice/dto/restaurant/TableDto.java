package com.rezervet.reservationservice.dto.restaurant;

import com.rezervet.reservationservice.enums.TableStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableDto {
    private UUID id;

    private UUID branchId;

    private String name;

    private int capacity;

    private String zone;

    private TableStatus status;
}
