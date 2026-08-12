package com.asanrezerv.restaurantservice.mapper;

import com.asanrezerv.restaurantservice.dto.table.TableDto;
import com.asanrezerv.restaurantservice.entity.RestaurantTable;
import org.springframework.stereotype.Service;

@Service
public class TableMapper {
    public TableDto tableToDto(RestaurantTable table) {
        return TableDto.builder()
                .id(table.getId())
                .branchId(table.getBranch().getId())
                .name(table.getName())
                .capacity(table.getCapacity())
                .zone(table.getZone())
                .status(table.getStatus())
                .type(table.getType())
                .build();
    }
}
