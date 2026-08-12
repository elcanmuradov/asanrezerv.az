package com.asanrezerv.subscriptionservice.repository;

import com.asanrezerv.subscriptionservice.entity.Subscription;
import com.asanrezerv.subscriptionservice.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    Optional<Subscription> findSubscriptionByRestaurantIdAndStatus(UUID restaurantId, SubscriptionStatus status);

    List<Subscription> findSubscriptionsByStatusAndEndDateBefore(SubscriptionStatus status, LocalDate endDateBefore);

    long countByStatus(SubscriptionStatus status);
}
