package com.asanrezerv.searchservice.service;

import com.asanrezerv.searchservice.document.BranchDocument;
import com.asanrezerv.searchservice.document.RestaurantDocument;
import com.asanrezerv.searchservice.exception.NotFoundException;
import com.asanrezerv.searchservice.repository.BranchSearchRepository;
import com.asanrezerv.searchservice.repository.RestaurantSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final RestaurantSearchRepository restaurantSearchRepository;
    private final BranchSearchRepository branchSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Cacheable(value = "catalog", key = "'restaurant:' + #id", unless = "#result == null")
    public RestaurantDocument getRestaurantById(String id) {
        return restaurantSearchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Restoran tapılmadı"));
    }

    public Page<BranchDocument> getBranchesByRestaurant(String restaurantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return branchSearchRepository.findByRestaurantId(restaurantId, pageable);
    }

    public BranchDocument getBranchById(String id) {
        return branchSearchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Filial tapılmadı"));
    }

    // DİQQƏT: Page<T> (PageImpl) daxilində Pageable sahəsi var — Jackson Redis-dən oxuyanda
    // abstrakt Pageable-i bərpa edə bilmir, ona görə burada cache-lənmir (bax: getRestaurantById).
    public Page<RestaurantDocument> getRestaurants(Pageable pageable) {
         return restaurantSearchRepository.findByVisibilityLevelGreaterThanAndPublicationStatus(0, "PUBLISHED", pageable);
    }

    public Page<RestaurantDocument> searchRestaurants(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "visibilityLevel"));
        return restaurantSearchRepository.findByPublicationStatusAndNameContainingIgnoreCaseOrPublicationStatusAndDescriptionContainingIgnoreCaseAndVisibilityLevelGreaterThan(
                "PUBLISHED", query, "PUBLISHED", query, 0, pageable);
    }

    public List<BranchDocument> getMapBranches() {
        Set<String> publishedRestaurantIds = StreamSupport
                .stream(restaurantSearchRepository.findAll(PageRequest.of(0, 1000)).spliterator(), false)
                .filter(r -> "PUBLISHED".equals(r.getPublicationStatus()))
                .map(RestaurantDocument::getId)
                .collect(Collectors.toSet());

        return StreamSupport
                .stream(branchSearchRepository.findAll(PageRequest.of(0, 1000)).spliterator(), false)
                .filter(b -> b.getLocation() != null && publishedRestaurantIds.contains(b.getRestaurantId()))
                .toList();
    }

//    public Page<RestaurantDocument> filterRestaurants(
//            String city, List<String> cuisineTypes, String priceRange, int page, int size) {
//
//        Pageable pageable = PageRequest.of(page, size,
//                Sort.by(Sort.Direction.DESC, "visibilityLevel")
//        );
//
//
//        // Dinamik sorğu qurmaq üçün NativeSearchQueryBuilder istifadə edirik
//        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
//
//        BoolQueryBuilder boolQuery = QueryBuilders.bool()
//                .must(QueryBuilders.ge("isActive", true))
//                .must(QueryBuilders.rangeQuery("visibilityLevel").gt(0));
//
//        if (city != null && !city.isBlank()) {
//            boolQuery.filter(QueryBuilders.termQuery("city", city));
//        }
//        if (cuisineTypes != null && !cuisineTypes.isEmpty()) {
//            boolQuery.filter(QueryBuilders.termsQuery("cuisineTypes", cuisineTypes));
//        }
//        if (priceRange != null && !priceRange.isBlank()) {
//            boolQuery.filter(QueryBuilders.termQuery("priceRange", priceRange));
//        }
//
//        NativeSearchQuery searchQuery = queryBuilder
//                .withQuery(boolQuery)
//                .withPageable(pageable)
//                .build();
//
//        return elasticsearchOperations.search(searchQuery, RestaurantDocument.class)
//                .map(SearchHit::getContent);
//    }
//
//    // 4. TƏK RESTORAN
//    public RestaurantDocument getRestaurantById(String id) {
//        return searchRepository.findById(id)
//                .orElseThrow(() -> new ChangeSetPersister.NotFoundException("Restoran tapılmadı"));
//    }
//
//    public List<RestaurantDocument> getNearbyRestaurants(double lat, double lon, double distanceKm) {
//        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
//                .withQuery(QueryBuilders.boolQuery()
//                        .must(QueryBuilders.matchAllQuery())
//                        .filter(QueryBuilders.geoDistanceQuery("location")
//                                .point(lat, lon)
//                                .distance(distanceKm, DistanceUnit.KILOMETERS))
//                        .filter(QueryBuilders.rangeQuery("visibilityLevel").gt(0))
//                        .filter(QueryBuilders.termQuery("isActive", true))
//                )
//                .withSorts(Sort.geoDistanceSort("location", lat, lon)
//                        .order(SortOrder.Asc)
//                        .unit(DistanceUnit.Kilometers))
//                .withPageable(PageRequest.of(0, 20))
//                .build();
//
//        return elasticsearchOperations.search(searchQuery, RestaurantDocument.class)
//                .stream()
//                .map(SearchHit::getContent)
//                .toList();
//    }
}
