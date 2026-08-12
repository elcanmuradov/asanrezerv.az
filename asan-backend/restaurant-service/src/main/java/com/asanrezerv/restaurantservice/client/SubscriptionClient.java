package com.asanrezerv.restaurantservice.client;

import com.asanrezerv.restaurantservice.dto.ApiResponse;
import com.asanrezerv.restaurantservice.dto.clients.subscription.BranchQuotaUsage;
import com.asanrezerv.restaurantservice.dto.clients.subscription.QuotaLimits;
import com.asanrezerv.restaurantservice.dto.clients.subscription.RestaurantQuotaUsage;
import com.asanrezerv.restaurantservice.dto.clients.subscription.SubscriptionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "subscription-service", url = "${spring.services.subscription-service.url}/api/subscriptions")
public interface SubscriptionClient {

    @GetMapping("/limits")
    ApiResponse<QuotaLimits> getLimits(@RequestParam("id") UUID planId);

    @GetMapping("/restaurant/usage")
    ApiResponse<RestaurantQuotaUsage> getRestaurantUsage(@RequestParam("id") UUID restaurantId);

    @GetMapping("/branch/usage")
    ApiResponse<BranchQuotaUsage> getBranchUsage(@RequestParam("id") UUID branchId);

    @GetMapping("/current")
    ApiResponse<SubscriptionDto> getCurrentSubscription(@RequestParam("restaurant-id") UUID restaurantId);
}
