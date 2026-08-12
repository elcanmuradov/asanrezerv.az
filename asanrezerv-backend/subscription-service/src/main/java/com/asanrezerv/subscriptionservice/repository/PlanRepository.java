package com.asanrezerv.subscriptionservice.repository;

import com.asanrezerv.subscriptionservice.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlanRepository extends JpaRepository<Plan, UUID> {

}
