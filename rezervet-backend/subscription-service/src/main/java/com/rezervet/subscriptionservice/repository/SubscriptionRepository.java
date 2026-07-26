package com.rezervet.subscriptionservice.repository;

import com.rezervet.subscriptionservice.entity.Subscription;
import com.rezervet.subscriptionservice.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findSubscriptionByRestaurantId(UUID restaurantId);

    List<Subscription> findSubscriptionsByStatusAndEndDateBefore(SubscriptionStatus status, LocalDate endDateBefore);
}
