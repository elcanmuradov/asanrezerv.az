package com.rezervet.restaurantservice.mapper;

import com.rezervet.restaurantservice.dto.table.TableDto;
import com.rezervet.restaurantservice.entity.RestaurantTable;
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
