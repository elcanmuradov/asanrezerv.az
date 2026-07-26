package com.rezervet.restaurantservice.dto.restaurant.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Time;

@Data
public class RestaurantRequest {
    private String name;
    private String cuisine;
    private String city;
    private String description;
    private String phone;


    private MultipartFile bannerPhoto;
    private MultipartFile profilePhoto;
}
