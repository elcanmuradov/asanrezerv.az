package com.asanrezerv.searchservice.consumer;

import com.asanrezerv.searchservice.document.BranchDocument;
import com.asanrezerv.searchservice.document.RestaurantDocument;
import com.asanrezerv.searchservice.dto.kafka.branch.BranchDto;
import com.asanrezerv.searchservice.dto.kafka.restaurant.RestaurantDto;
import com.asanrezerv.searchservice.dto.kafka.subscription.RestaurantSubscribed;
import com.asanrezerv.searchservice.repository.BranchSearchRepository;
import com.asanrezerv.searchservice.repository.RestaurantSearchRepository;
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
                .publicationStatus(restaurant.getPublicationStatus() != null ? restaurant.getPublicationStatus() : "DRAFT")
                .createdAt(restaurant.getCreatedAt().toString())
                .updatedAt(restaurant.getUpdatedAt().toString())
                .build();


        var document = restaurantSearchRepository.save(d);
        log.info("Created restaurant {}", document);
    }

    @KafkaListener(topics = "restaurant.updated", containerFactory = "restaurant")
    public void restaurantUpdated(RestaurantDto restaurant) {
        var existing = restaurantSearchRepository.findById(restaurant.getId().toString()).orElse(null);
        // Profil sahələri yenilənir, amma abunə (visibilityLevel/aiAnalysisLevel) və
        // nəşr statusu bu event-in işi deyil — mövcud sənəddən olduğu kimi saxlanılır.
        RestaurantDocument document = RestaurantDocument.builder()
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
                .publicationStatus(existing != null ? existing.getPublicationStatus() : "DRAFT")
                .visibilityLevel(existing != null ? existing.getVisibilityLevel() : 0)
                .aiAnalysisLevel(existing != null ? existing.getAiAnalysisLevel() : 0)
                .createdAt(restaurant.getCreatedAt().toString())
                .updatedAt(restaurant.getUpdatedAt().toString())
                .build();

        restaurantSearchRepository.save(document);
    }

    @KafkaListener(topics = "restaurant.published", containerFactory = "uuid")
    public void restaurantPublished(UUID restaurantId) {
        restaurantSearchRepository.findById(restaurantId.toString())
                .ifPresentOrElse(document -> {
                    document.setPublicationStatus("PUBLISHED");
                    restaurantSearchRepository.save(document);
                }, () -> log.warn("restaurant.published: sənəd tapılmadı, restaurantId={}", restaurantId));
    }

    @KafkaListener(topics = "restaurant.subscribed", containerFactory = "subscription")
    public void restaurantSubscribed(RestaurantSubscribed subscription) {
        restaurantSearchRepository.findById(subscription.getRestaurantId().toString())
                .ifPresentOrElse(document -> {
                    document.setVisibilityLevel(subscription.getVisibilityLevel());
                    document.setAiAnalysisLevel(subscription.getAiAnalysisLevel());
                    restaurantSearchRepository.save(document);
                }, () -> log.warn("restaurant.subscribed: sənəd tapılmadı, restaurantId={}", subscription.getRestaurantId()));
    }

    @KafkaListener(topics = "restaurant.subscription.expired", containerFactory = "subscription")
    public void restaurantSubscriptionExpired(RestaurantSubscribed subscription) {
        restaurantSearchRepository.findById(subscription.getRestaurantId().toString())
                .ifPresentOrElse(document -> {
                    document.setVisibilityLevel(0);
                    document.setAiAnalysisLevel(0);
                    restaurantSearchRepository.save(document);
                }, () -> log.warn("restaurant.subscription.expired: sənəd tapılmadı, restaurantId={}", subscription.getRestaurantId()));
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
