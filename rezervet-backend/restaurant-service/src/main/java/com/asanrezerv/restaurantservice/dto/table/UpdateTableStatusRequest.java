package com.asanrezerv.restaurantservice.dto.table;

import com.asanrezerv.restaurantservice.enums.TableStatus;
import lombok.Data;

@Data
public class UpdateTableStatusRequest {
    private TableStatus status;
}