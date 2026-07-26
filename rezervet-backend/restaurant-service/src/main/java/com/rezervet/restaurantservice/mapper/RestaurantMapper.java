package com.rezervet.restaurantservice.mapper;

import com.rezervet.restaurantservice.dto.restaurant.MediaAssetsDto;
import com.rezervet.restaurantservice.dto.restaurant.RestaurantDto;
import com.rezervet.restaurantservice.entity.Restaurant;
import com.rezervet.restaurantservice.enums.PhotoType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class RestaurantMapper {

    public RestaurantDto toRestaurantDto(Restaurant restaurant) {

        AtomicReference<String> profileUrl = new AtomicReference<>();
        AtomicReference<String> bannerUrl = new AtomicReference<>();
        List<String> menuItems = new ArrayList<>();
        List<String> galleryUrls = new ArrayList<>();

        restaurant.getMediaList().forEach((media) -> {
            if (media.getMediaType().equals(PhotoType.BANNER)) {
                bannerUrl.set(media.getImageUrl());
            } else if (media.getMediaType().equals(PhotoType.GALLERY)) {
                galleryUrls.add(media.getImageUrl());
            } else if (media.getMediaType().equals(PhotoType.PROFILE)) {
                profileUrl.set(media.getImageUrl());
            } else {
                menuItems.add(media.getImageUrl());
            }
        });

        MediaAssetsDto mediaAssetsDto = MediaAssetsDto.builder()
                .profilePhotoUrl(profileUrl.get())
                .bannerUrl(bannerUrl.get())
                .galleryUrls(galleryUrls)
                .menuItemsUrls(menuItems)
                .build();

        return RestaurantDto.builder()
                .id(restaurant.getId())
                .ownerId(restaurant.getOwnerId())
                .name(restaurant.getName())
                .cuisine(restaurant.getCuisine())
                .city(restaurant.getCity())
                .description(restaurant.getDescription())
                .phone(restaurant.getPhone())
                .mediaAssets(mediaAssetsDto)
                .createdAt(restaurant.getCreatedAt())
                .updatedAt(restaurant.getUpdatedAt())
                .build();
    }

}
