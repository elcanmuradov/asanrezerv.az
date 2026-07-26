package com.rezervet.searchservice.consumer;

import com.rezervet.searchservice.document.BranchDocument;
import com.rezervet.searchservice.document.RestaurantDocument;
import com.rezervet.searchservice.dto.kafka.branch.BranchDto;
import com.rezervet.searchservice.dto.kafka.restaurant.RestaurantDto;
import com.rezervet.searchservice.dto.kafka.subscription.RestaurantSubscribed;
import com.rezervet.searchservice.repository.BranchSearchRepository;
import com.rezervet.searchservice.repository.RestaurantSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantConsumer {
    private final RestaurantSearchRepository restaurantSearchRepository;
    private final BranchSearchRepository branchSearchRepository;

    @KafkaListener(topics = "restaurant.created", containerFactory = "restaurant")
    public void restaurantCreated(RestaurantDto restaurant) {
        RestaurantDocument d = RestaurantDocument.builder()
                .id(restaurant.getId().toString())
                .ownerId(restaurant.getOwnerId().toString())
                .name(restaurant.getName())
                .cuisine(restaurant.getCuisine())
                .city(restaurant.getCity())
                .description(restaurant.getDescription())
                .phone(restaurant.getPhone())
                .bannerUrl(restaurant.getMediaAssets().getBannerUrl())
                .profilePhotoUrl(restaurant.getMediaAssets().getProfilePhotoUrl())
                .galleryUrls(restaurant.getMediaAssets().getGalleryUrls())
                .menuImages(restaurant.getMediaAssets().getMenuItemsUrls())
                .createdAt(restaurant.getCreatedAt())
                .updatedAt(restaurant.getUpdatedAt())
                .build();

        restaurantSearchRepository.save(d);
    }

    @KafkaListener(topics = "restaurant.updated", containerFactory = "restaurant")
    public void restaurantUpdated(RestaurantDto restaurant) {
        var document = restaurantSearchRepository.findById(restaurant.getId().toString()).orElse(new RestaurantDocument());
        document = RestaurantDocument.builder()
                .id(restaurant.getId().toString())
                .ownerId(restaurant.getOwnerId().toString())
                .name(restaurant.getName())
                .cuisine(restaurant.getCuisine())
                .city(restaurant.getCity())
                .description(restaurant.getDescription())
                .phone(restaurant.getPhone())
                .bannerUrl(restaurant.getMediaAssets().getBannerUrl())
                .profilePhotoUrl(restaurant.getMediaAssets().getProfilePhotoUrl())
                .galleryUrls(restaurant.getMediaAssets().getGalleryUrls())
                .menuImages(restaurant.getMediaAssets().getMenuItemsUrls())
                .createdAt(restaurant.getCreatedAt())
                .updatedAt(restaurant.getUpdatedAt())
                .build();

        restaurantSearchRepository.save(document);
    }

    @KafkaListener(topics = "restaurant.subscribed", containerFactory = "subscription")
    public void restaurantSubscribed(RestaurantSubscribed subscription) {
        RestaurantDocument document = restaurantSearchRepository.findById(subscription.getRestaurantId().toString()).orElseThrow();

        document.setVisibilityLevel(subscription.getVisibilityLevel());
        document.setAiAnalysisLevel(subscription.getAiAnalysisLevel());
        restaurantSearchRepository.save(document);
    }

    @KafkaListener(topics = "restaurant.subscription.expired", containerFactory = "subscription")
    public void restaurantSubscriptionExpired(RestaurantSubscribed subscription) {
        var document = restaurantSearchRepository.findById(subscription.getRestaurantId().toString()).orElseThrow();
        document.setVisibilityLevel(0);
        document.setAiAnalysisLevel(0);
        restaurantSearchRepository.save(document);

    }

    @KafkaListener(topics = "branch.created", containerFactory = "branch")
    public void branchCreated(BranchDto dto) {
        saveBranchDocument(dto);
    }

    @KafkaListener(topics = "branch.updated", containerFactory = "branch")
    public void branchUpdated(BranchDto dto) {
        saveBranchDocument(dto);
    }

    private void saveBranchDocument(BranchDto dto) {
        // Restoran adı üçün onsuz da index-də olan RestaurantDocument-ə baxırıq
        // (restaurant.created "branch.created"-dən əvvəl işlənmiş olmalıdır — eyni servisin öz produced sırası budur).
        String restaurantName = restaurantSearchRepository.findById(dto.getRestaurantId().toString())
                .map(RestaurantDocument::getName)
                .orElse(null);

        GeoPoint location = (dto.getLatitude() != null && dto.getLongitude() != null)
                ? new GeoPoint(dto.getLatitude(), dto.getLongitude())
                : null;

        BranchDocument branch = BranchDocument.builder()
                .id(dto.getBranchId().toString())
                .restaurantId(dto.getRestaurantId().toString())
                .restaurantName(restaurantName)
                .branchName(dto.getName())
                .city(dto.getCity())
                .district(dto.getDistrict())
                .address(dto.getAddress())
                .location(location)
                .minTableCapacity(dto.getMinTableCapacity() != null ? dto.getMinTableCapacity() : 0)
                .maxTableCapacity(dto.getMaxTableCapacity() != null ? dto.getMaxTableCapacity() : 0)
                .workingHours(dto.getOpeningTime() + " - " + dto.getClosingTime())
                .createdAt(dto.getCreatedAt() != null ? dto.getCreatedAt().toString() : null)
                .updatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt().toString() : null)
                .build();

        branchSearchRepository.save(branch);
    }

    @KafkaListener(topics = "branch.search.deleted", containerFactory = "uuid")
    public void branchDeleted(UUID branchId) {
        branchSearchRepository.deleteById(branchId.toString());
    }

}
