package com.rezervet.subscriptionservice.repository;

import com.rezervet.subscriptionservice.entity.Plan;
import com.rezervet.subscriptionservice.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlanRepository extends JpaRepository<Plan, UUID> {

}
