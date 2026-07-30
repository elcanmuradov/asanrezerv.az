package com.asanrezerv.subscriptionservice.controller;

import com.asanrezerv.subscriptionservice.dto.ApiResponse;
import com.asanrezerv.subscriptionservice.dto.client.BranchQuotaUsageDto;
import com.asanrezerv.subscriptionservice.dto.client.QuotaLimit;
import com.asanrezerv.subscriptionservice.dto.client.RestaurantQuotaUsageDto;
import com.asanrezerv.subscriptionservice.dto.controller.PlanDto;
import com.asanrezerv.subscriptionservice.dto.controller.SubscriptionDto;
import com.asanrezerv.subscriptionservice.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/limits")
    public ResponseEntity<ApiResponse<QuotaLimit>> getLimits(@RequestParam("id") UUID restaurantId) {
        return ResponseEntity.ok(ApiResponse.success(subscriptionService.getLimits(restaurantId)));
    }

    // ai-analysis-service bunu oxuyur: restoranın AI analiz səviyyəsi (0/1/2)
    @GetMapping("/ai-level")
    public ResponseEntity<ApiResponse<Integer>> getAiLevel(@RequestParam("id") UUID restaurantId) {
        return ResponseEntity.ok(ApiResponse.success(subscriptionService.getAiLevel(restaurantId)));
    }

    @GetMapping("/restaurant/usage")
    public ResponseEntity<ApiResponse<RestaurantQuotaUsageDto>> getRestaurantUsage(@RequestParam("id") UUID restaurantId) {
        return ResponseEntity.ok(ApiResponse.success(subscriptionService.getRestaurantUsage(restaurantId)));
    }

    @GetMapping("/branch/usage")
    public ResponseEntity<ApiResponse<BranchQuotaUsageDto>> getBranchUsage(@RequestParam("id") UUID branchId){
        return ResponseEntity.ok(ApiResponse.success(subscriptionService.getBranchQuotaUsage(branchId)));
    }

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<PlanDto>>> getPlans() {
       return ResponseEntity.ok(ApiResponse.success(subscriptionService.getPlans()));
    }
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<SubscriptionDto>> getCurrent(@RequestParam("restaurant-id")  UUID restaurantId) {
        return ResponseEntity.ok(ApiResponse.success(subscriptionService.currentSubscription(restaurantId)));
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<SubscriptionDto>> checkout(@RequestParam("restaurant-id") UUID restaurantId, @RequestParam("plan-id") UUID planId) {
        return ResponseEntity.ok(ApiResponse.success(subscriptionService.checkout(restaurantId,planId)));

    }

}

/*

subscription-service Plan entity-yə sahə əlavə et:
description (String, mətn), popular (boolean), features (List<String> — @ElementCollection və ya JSONB sütun). visibilityLevel onsuz da var.
Sahə adlarını camelCase-ə uyğunlaşdır (monthlyPrice, maxBranches, maxTablesPerBranch, visibilityLevel) və ya Jackson-u snake_case üçün konfiqurasiya et. Hazırda entity snake_case-dir (monthly_price, max_branches), frontend isə camelCase oxuyur → bu, indiki public Pricing səhifəsində qiymətin — görünməsinin də səbəbidir. camelCase tövsiyə olunur.
Admin endpoint-ləri (subscription-service):

PlanDto-ya çatışmayan sahələri (visibilityLevel, description, features, popular) əlavə et.
Gateway RouteConfig.java: yeni route — /api/admin/plans/** → subscriptionUri. (/api/admin/restaurants/** restaurant-service-ə gedir; path-lar kəsişmir, ayrıca route kifayətdir.)


 */
