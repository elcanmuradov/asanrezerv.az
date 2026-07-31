package com.asanrezerv.restaurantservice.dto.branch;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
    private String googleMapsLink;
    private List<String> photosUrl;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime openingTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime closingTime;

    private LocalDateTime createdAt;
}
