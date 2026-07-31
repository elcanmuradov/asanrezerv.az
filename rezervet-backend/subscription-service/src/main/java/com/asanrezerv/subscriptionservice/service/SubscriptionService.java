package com.asanrezerv.subscriptionservice.service;

import com.asanrezerv.subscriptionservice.dto.client.BranchQuotaUsageDto;
import com.asanrezerv.subscriptionservice.dto.client.QuotaLimit;
import com.asanrezerv.subscriptionservice.dto.client.RestaurantQuotaUsageDto;
import com.asanrezerv.subscriptionservice.dto.controller.PlanDto;
import com.asanrezerv.subscriptionservice.dto.controller.SubscriptionDto;
import com.asanrezerv.subscriptionservice.dto.kafka.RestaurantSubscribed;
import com.asanrezerv.subscriptionservice.entity.BranchQuotaUsage;
import com.asanrezerv.subscriptionservice.entity.Plan;
import com.asanrezerv.subscriptionservice.entity.RestaurantQuotaUsage;
import com.asanrezerv.subscriptionservice.entity.Subscription;
import com.asanrezerv.subscriptionservice.enums.SubscriptionStatus;
import com.asanrezerv.subscriptionservice.exception.NotFoundException;
import com.asanrezerv.subscriptionservice.repository.BranchUsageRepository;
import com.asanrezerv.subscriptionservice.repository.PlanRepository;
import com.asanrezerv.subscriptionservice.repository.RestaurantUsageRepository;
import com.asanrezerv.subscriptionservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final RestaurantUsageRepository restaurantUsageRepository;
    private final BranchUsageRepository branchUsageRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    public QuotaLimit getLimits(UUID restaurantId) {

        var opt = subscriptionRepository.findSubscriptionByRestaurantIdAndStatus(restaurantId,SubscriptionStatus.ACTIVE);


        if (opt.isEmpty()) {
            return new QuotaLimit(0,0);
        }

        var plan = opt.get().getPlan();



        return new QuotaLimit(plan.getMaxBranches(),plan.getMaxTablesPerBranch());

    }

    public Integer getAiLevel(UUID restaurantId) {
        var opt = subscriptionRepository.findSubscriptionByRestaurantIdAndStatus(restaurantId,SubscriptionStatus.ACTIVE);
        if (opt.isEmpty()) {
            return 0;
        }
        Integer level = opt.get().getPlan().getAiAnalysisLevel();
        return level == null ? 0 : level;
    }

    public RestaurantQuotaUsageDto getRestaurantUsage(UUID restaurantId) {
        var opt = restaurantUsageRepository.findRestaurantQuotaUsageByRestaurantId(restaurantId);

        if (opt.isEmpty()) {
            throw new NotFoundException("QuotaUsage not found");
        }

        return restaurantUsageToDto(opt.get());

    }

    public BranchQuotaUsageDto getBranchQuotaUsage(UUID branchId) {
        var opt = branchUsageRepository.findBranchQuotaUsageByBranchId(branchId);
        if (opt.isEmpty()) {
            throw new NotFoundException("QuotaUsage not found");
        }
        return branchUsageToDto(opt.get());
    }

    public List<PlanDto> getPlans() {
        List<PlanDto> plans = new ArrayList<>();
        planRepository.findAll().forEach(plan -> plans.add(planToDto(plan)));
        return plans;
    }

    public SubscriptionDto currentSubscription(UUID restaurantId) {
        var opt =subscriptionRepository.findSubscriptionByRestaurantIdAndStatus(restaurantId,SubscriptionStatus.ACTIVE);
        if (opt.isEmpty()) {
            throw new NotFoundException("Subscription not found");
        }
        return subscriptionToDto(opt.get());
    }

    public SubscriptionDto checkout(UUID restaurantId, UUID planId) {

        var opt = planRepository.findById(planId);
        if (opt.isEmpty()) {
            throw new NotFoundException("Plan not found");
        }
        var plan = opt.get();

        Subscription subscription = Subscription.builder()
                .restaurantId(restaurantId)
                .plan(plan)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .status(SubscriptionStatus.ACTIVE)
                .build();

        subscription = subscriptionRepository.save(subscription);


        RestaurantSubscribed event = RestaurantSubscribed.builder()
                .aiAnalysisLevel(plan.getAiAnalysisLevel())
                .visibilityLevel(plan.getVisibilityLevel())
                .restaurantId(restaurantId)
                .build();

        kafkaTemplate.send("restaurant.subscribed",event);

        return subscriptionToDto(subscription);

    }


    private RestaurantQuotaUsageDto restaurantUsageToDto(RestaurantQuotaUsage usage) {
        return RestaurantQuotaUsageDto.builder()
                .id(usage.getId())
                .restaurantId(usage.getRestaurantId())
                .currentBranchCount(usage.getCurrentBranchCount())
                .build();
    }

    private BranchQuotaUsageDto branchUsageToDto(BranchQuotaUsage usage) {
        return BranchQuotaUsageDto.builder()
                .id(usage.getId())
                .branchId(usage.getBranchId())
                .currentTableCount(usage.getCurrentTableCount())
                .build();
    }


    private PlanDto planToDto(Plan plan) {
        return PlanDto.builder()
                .id(plan.getId())
                .name(plan.getName())
                .maxBranches(plan.getMaxBranches())
                .maxTablesPerBranch(plan.getMaxTablesPerBranch())
                .monthlyPrice(plan.getMonthlyPrice())
                .description(plan.getDescription())
                .mostPopular(plan.isMostPopular())
                .features(plan.getFeatures())
                .visibilityLevel(plan.getVisibilityLevel())
                .build();
    }

    private SubscriptionDto subscriptionToDto(Subscription subscription){
        return SubscriptionDto.builder()
                .id(subscription.getId())
                .restaurantId(subscription.getRestaurantId())
                .planId(subscription.getPlan().getId())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .status(subscription.getStatus())
                .build();
    }





}
