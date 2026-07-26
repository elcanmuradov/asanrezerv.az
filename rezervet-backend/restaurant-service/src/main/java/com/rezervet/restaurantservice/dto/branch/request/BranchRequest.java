package com.rezervet.restaurantservice.dto.branch.request;

import lombok.Data;

import java.sql.Time;

@Data
public class BranchRequest {
    private String name;
    private String address;
    private String phone;

    // search-service-də BranchDocument üçün (opsional — boş qalsa restoranın şəhəri istifadə olunur)
    private String city;
    private String district;
    private Double latitude;
    private Double longitude;

    private Time openingTime;
    private Time closingTime;
}
