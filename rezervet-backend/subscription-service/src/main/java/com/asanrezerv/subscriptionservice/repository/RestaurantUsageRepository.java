package com.asanrezerv.subscriptionservice.repository;

import com.asanrezerv.subscriptionservice.entity.RestaurantQuotaUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RestaurantUsageRepository extends JpaRepository<RestaurantQuotaUsage, UUID> {

    Optional<RestaurantQuotaUsage> findRestaurantQuotaUsageByRestaurantId(UUID restaurantId);

}
