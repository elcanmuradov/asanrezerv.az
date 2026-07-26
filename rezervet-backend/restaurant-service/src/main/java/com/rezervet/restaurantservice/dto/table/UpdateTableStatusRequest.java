package com.rezervet.restaurantservice.dto.table;

import com.rezervet.restaurantservice.enums.TableStatus;
import lombok.Data;

@Data
public class UpdateTableStatusRequest {
    private TableStatus status;
}