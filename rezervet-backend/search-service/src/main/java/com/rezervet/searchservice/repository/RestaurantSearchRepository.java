package com.rezervet.searchservice.repository;

import com.rezervet.searchservice.document.RestaurantDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RestaurantSearchRepository extends ElasticsearchRepository<RestaurantDocument, String> {

    Page<RestaurantDocument> findByVisibilityLevelGreaterThan(
            int minVisibilityLevel,
            Pageable pageable
    );

    // Axtarış üçün: Adında və ya təsvirində söz keçən, aktiv restoranlar
    Page<RestaurantDocument> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndVisibilityLevelGreaterThan(
            String nameQuery,
            int minVisibilityLevel,
            Pageable pageable
    );

    // Filtrləmə üçün: Şəhər, Mətbəx və Qiymət aralığına görə
    Page<RestaurantDocument> findByCityAndCuisineTypesInAndPriceRangeAndIsActiveTrue(
            String city,
            List<String> cuisineTypes,
            String priceRange,
            Pageable pageable
    );


}