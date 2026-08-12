package com.asanrezerv.restaurantservice.dto.restaurant;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MediaAssetsDto{
        private String profilePhotoUrl;
        private String bannerUrl;
        private List<String> galleryUrls;
        private List<String> menuItemsUrls;
}