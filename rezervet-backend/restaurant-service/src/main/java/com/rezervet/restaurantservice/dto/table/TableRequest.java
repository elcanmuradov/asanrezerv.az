package com.rezervet.restaurantservice.dto.table;

import com.rezervet.restaurantservice.enums.TableType;
import lombok.Data;

@Data
public class TableRequest {
    private String name;
    private int capacity;
    private String zone;
    private TableType type;
}
