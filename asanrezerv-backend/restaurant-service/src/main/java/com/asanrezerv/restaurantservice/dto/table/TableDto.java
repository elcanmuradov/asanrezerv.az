package com.asanrezerv.restaurantservice.dto.table;

import com.asanrezerv.restaurantservice.enums.TableStatus;
import com.asanrezerv.restaurantservice.enums.TableType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TableDto {
    private UUID id;

    private UUID branchId;

    private String name;

    private int capacity;

    private String zone;

    private TableStatus status;

    private TableType type ;
}
