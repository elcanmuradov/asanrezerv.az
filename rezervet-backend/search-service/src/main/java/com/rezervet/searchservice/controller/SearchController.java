package com.rezervet.searchservice.controller;

import com.rezervet.searchservice.document.BranchDocument;
import com.rezervet.searchservice.document.RestaurantDocument;
import com.rezervet.searchservice.dto.ApiResponse;
import com.rezervet.searchservice.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/restaurants")
    public ResponseEntity<ApiResponse<Page<RestaurantDocument>>> getRestaurants(@RequestParam(defaultValue = "1") Integer pageNumber,@RequestParam(defaultValue = "20") Integer pageSize) {
        return ResponseEntity.ok(ApiResponse.success(searchService.getRestaurants(pageNumber,pageSize)));
    }

    @GetMapping("/restaurants/search")
    public ResponseEntity<ApiResponse<Page<RestaurantDocument>>> searchRestaurants(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(ApiResponse.success(searchService.searchRestaurants(query, page, size)));
    }

    // Tək restoranın detalı (frontend-in public restoran səhifəsi üçün)
    @GetMapping("/restaurants/{id}")
    public ResponseEntity<ApiResponse<RestaurantDocument>> getRestaurantById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(searchService.getRestaurantById(id)));
    }

    // Bir restoranın bütün filialları (frontend-in restoran səhifəsindəki filial siyahısı üçün)
    @GetMapping("/restaurants/{id}/branches")
    public ResponseEntity<ApiResponse<Page<BranchDocument>>> getBranchesByRestaurant(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(searchService.getBranchesByRestaurant(id, page, size)));
    }

}
