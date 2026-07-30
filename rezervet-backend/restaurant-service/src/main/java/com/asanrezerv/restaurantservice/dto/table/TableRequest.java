package com.asanrezerv.restaurantservice.dto.table;

import com.asanrezerv.restaurantservice.enums.TableType;
import lombok.Data;

@Data
public class TableRequest {
    private String name;
    private int capacity;
    private String zone;
    private TableType type;
}
