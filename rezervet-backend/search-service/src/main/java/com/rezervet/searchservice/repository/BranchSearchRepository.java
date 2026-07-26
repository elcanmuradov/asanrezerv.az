package com.rezervet.searchservice.repository;

import com.rezervet.searchservice.document.BranchDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BranchSearchRepository extends ElasticsearchRepository<BranchDocument, String> {

    // Bir restoranın bütün filialları (manager/public üçün)
    Page<BranchDocument> findByRestaurantId(String restaurantId, Pageable pageable);

    // Filial adı və ya şəhər üzrə axtarış
    Page<BranchDocument> findByBranchNameContainingIgnoreCaseOrCityContainingIgnoreCase(
            String branchNameQuery, String cityQuery, Pageable pageable);
}
