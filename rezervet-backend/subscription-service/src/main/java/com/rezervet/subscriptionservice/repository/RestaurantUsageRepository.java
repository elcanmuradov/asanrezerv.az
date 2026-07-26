package com.rezervet.subscriptionservice.repository;

import com.rezervet.subscriptionservice.entity.RestaurantQuotaUsage;
import com.rezervet.subscriptionservice.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RestaurantUsageRepository extends JpaRepository<RestaurantQuotaUsage, UUID> {

    Optional<RestaurantQuotaUsage> findRestaurantQuotaUsageByRestaurantId(UUID restaurantId);

}
