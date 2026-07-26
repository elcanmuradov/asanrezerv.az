package com.rezervet.searchservice.dto.kafka.restaurant;

import lombok.Data;

import java.util.List;

@Data
public class MediaAssetsDto {
    private String profilePhotoUrl;
    private String bannerUrl;
    private List<String> galleryUrls;
    private List<String> menuItemsUrls;
}
