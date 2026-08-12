package com.asanrezerv.restaurantservice.dto.branch.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Time;
import java.time.LocalTime;

@Data
public class BranchRequest {
    private String name;
    private String address;
    private String phone;

    private String city;

    private String district;

    // Menecer Google Maps-dən kopyaladığı linki verir; enlik/uzunluq bundan server tərəfdə çıxarılır.
    private String googleMapsLink;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime openingTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime closingTime;
}
