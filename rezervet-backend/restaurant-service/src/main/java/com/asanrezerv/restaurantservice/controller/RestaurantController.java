package com.asanrezerv.restaurantservice.controller;

import com.asanrezerv.restaurantservice.dto.ApiResponse;
import com.asanrezerv.restaurantservice.dto.branch.BranchDto;
import com.asanrezerv.restaurantservice.dto.branch.request.BranchRequest;
import com.asanrezerv.restaurantservice.dto.restaurant.RestaurantDto;
import com.asanrezerv.restaurantservice.dto.restaurant.request.RestaurantRequest;
import com.asanrezerv.restaurantservice.dto.table.TableDto;
import com.asanrezerv.restaurantservice.dto.table.TableRequest;
import com.asanrezerv.restaurantservice.enums.PhotoType;
import com.asanrezerv.restaurantservice.enums.TableStatus;
import com.asanrezerv.restaurantservice.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
public class RestaurantController {
    private final RestaurantService restaurantService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RestaurantDto>>> getRestaurants(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                restaurantService.getRestaurants(search, city, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantDto>> getRestaurantById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(restaurantService.getRestaurantById(id)));
    }

    @GetMapping("/{id}/branches")
    public ResponseEntity<ApiResponse<List<BranchDto>>> getBranchesByRestaurant(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(restaurantService.getBranchesByRestaurant(id)));
    }

    @GetMapping("/branches/{branchId}/tables")
    public ResponseEntity<ApiResponse<List<TableDto>>> getTables(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID branchId) {
        return ResponseEntity.ok(ApiResponse.success(restaurantService.getTables(userId, branchId)));
    }

    @PatchMapping("/tables/change-status")
    public ResponseEntity<ApiResponse<TableDto>> changeTableStatus(@RequestParam(name = "status") TableStatus status,@RequestParam(name = "tableId") UUID id) {
        return  ResponseEntity.ok(ApiResponse.success(restaurantService.changeTableStatus(id,status)));
    }


    // Management

    @GetMapping("/restaurant")
    public ResponseEntity<ApiResponse<RestaurantDto>> getUserRestaurant(
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(restaurantService.getUserRestaurant(userId)));
    }

    // Boşluğu bağlayır: yeni menecer öz restoranını yaradır (ownerId = userId)
    @PostMapping("/restaurant")
    public ResponseEntity<ApiResponse<RestaurantDto>> createMyRestaurant(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody RestaurantRequest request) {
        return ResponseEntity.ok(ApiResponse.success(restaurantService.createMyRestaurant(userId, request)));
    }

    @PostMapping("/restaurant/image")
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam("type") PhotoType type,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success(restaurantService.uploadImage(userId, type, file)));
    }

    @PutMapping("/restaurant")
    public ResponseEntity<ApiResponse<RestaurantDto>> updateMyRestaurant(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody RestaurantRequest request) {
        return ResponseEntity.ok(ApiResponse.success(restaurantService.updateMyRestaurant(userId, request)));
    }

    @PatchMapping("/{id}/publish")
    public void publishRestaurant(@PathVariable UUID id) {
         restaurantService.publishRestaurant(id);
    }

    // ---- Filiallar ----
    @GetMapping("/branches")
    public ResponseEntity<ApiResponse<List<BranchDto>>> getBranches(
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(restaurantService.getBranches(userId)));
    }

    @PostMapping("/branches")
    public ResponseEntity<ApiResponse<BranchDto>> createBranch(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody BranchRequest request) {
        return ResponseEntity.ok(ApiResponse.success(restaurantService.createBranch(userId, request)));
    }

    @PutMapping("/branches/{id}")
    public ResponseEntity<ApiResponse<BranchDto>> updateBranch(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id,
            @RequestBody BranchRequest request) {
        return ResponseEntity.ok(ApiResponse.success(restaurantService.updateBranch(userId, id, request)));
    }

    @DeleteMapping("/branches/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBranch(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id) {
        restaurantService.deleteBranch(userId, id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ---- Masalar ----


    @PostMapping("/branches/{branchId}/tables")
    public ResponseEntity<ApiResponse<TableDto>> createTable(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID branchId,
            @RequestBody TableRequest request) {
        return ResponseEntity.ok(ApiResponse.success(restaurantService.createTable(userId, branchId, request)));
    }

    @PutMapping("/tables/{tableId}")
    public ResponseEntity<ApiResponse<TableDto>> updateTable(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID tableId,
            @RequestBody TableRequest request) {
        return ResponseEntity.ok(ApiResponse.success(restaurantService.updateTable(userId, tableId, request)));
    }

    @DeleteMapping("/tables/{tableId}")
    public ResponseEntity<ApiResponse<Void>> deleteTable(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID tableId) {
        restaurantService.deleteTable(userId, tableId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
