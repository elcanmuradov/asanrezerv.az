package com.asanrezerv.restaurantservice.dto.branch;

import lombok.Builder;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BranchDto {
    private UUID id;

    private UUID restaurantId;

    private String name;

    private String address;

    private String phone;

    private String city;
    private String district;
    private Double latitude;
    private Double longitude;

    private Time openingTime;

    private Time closingTime;

    private LocalDateTime createdAt;
}
