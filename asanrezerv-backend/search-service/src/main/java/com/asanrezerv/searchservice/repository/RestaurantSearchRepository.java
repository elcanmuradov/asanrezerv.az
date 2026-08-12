package com.asanrezerv.searchservice.repository;

import com.asanrezerv.searchservice.document.RestaurantDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantSearchRepository extends ElasticsearchRepository<RestaurantDocument, String> {

    Page<RestaurantDocument> findByVisibilityLevelGreaterThanAndPublicationStatus(
            int minVisibilityLevel,
            String publicationStatus,
            Pageable pageable
    );

    // Axtarış üçün: Adında və ya təsvirində söz keçən, dərc olunmuş, aktiv restoranlar
    // Qruplaşma: (publicationStatus=? AND name LIKE ?) OR (publicationStatus=? AND description LIKE ? AND visibilityLevel > ?)
    Page<RestaurantDocument> findByPublicationStatusAndNameContainingIgnoreCaseOrPublicationStatusAndDescriptionContainingIgnoreCaseAndVisibilityLevelGreaterThan(
            String publicationStatus1, String name,
            String publicationStatus2, String description, Integer visibilityLevel,
            Pageable pageable
    );

}