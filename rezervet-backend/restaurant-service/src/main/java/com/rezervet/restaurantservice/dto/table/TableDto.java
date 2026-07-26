package com.rezervet.restaurantservice.dto.table;

import com.rezervet.restaurantservice.entity.Branch;
import com.rezervet.restaurantservice.enums.TableStatus;
import com.rezervet.restaurantservice.enums.TableType;
import jakarta.persistence.*;
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
