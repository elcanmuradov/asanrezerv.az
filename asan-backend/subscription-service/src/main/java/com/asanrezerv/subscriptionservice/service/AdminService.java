package com.asanrezerv.subscriptionservice.service;

import com.asanrezerv.subscriptionservice.client.AuthClient;
import com.asanrezerv.subscriptionservice.client.ReservationClient;
import com.asanrezerv.subscriptionservice.client.RestaurantClient;
import com.asanrezerv.subscriptionservice.dto.controller.AdminStatsDto;
import com.asanrezerv.subscriptionservice.dto.controller.AdminSubscriptionDto;
import com.asanrezerv.subscriptionservice.dto.controller.PlanDto;
import com.asanrezerv.subscriptionservice.dto.controller.PlanRequest;
import com.asanrezerv.subscriptionservice.entity.Plan;
import com.asanrezerv.subscriptionservice.entity.Subscription;
import com.asanrezerv.subscriptionservice.enums.SubscriptionStatus;
import com.asanrezerv.subscriptionservice.exception.NotFoundException;
import com.asanrezerv.subscriptionservice.repository.PlanRepository;
import com.asanrezerv.subscriptionservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final RestaurantClient restaurantClient;
    private final AuthClient authClient;
    private final ReservationClient reservationClient;

    public List<PlanDto> getPlans() {
        List<PlanDto> plans = new ArrayList<>();

        planRepository.findAll().forEach(plan -> {
            plans.add(planToDto(plan));
        });

        return plans;



    }

    public PlanDto updatePlan(UUID id, PlanRequest request) {
        var opt =  planRepository.findById(id);

        if (opt.isEmpty()) {
            throw new NotFoundException("Plan not found");
        }
        var plan = opt.get();
        plan.setName(request.getName());
        plan.setMonthlyPrice(request.getMonthlyPrice());
        plan.setMaxBranches(request.getMaxBranches());
        plan.setMaxTablesPerBranch(request.getMaxTablesPerBranch());
        plan.setDescription(request.getDescription());
        plan.setMostPopular(request.isMostPopular());
        plan.setFeatures(request.getFeatures());
        plan.setVisibilityLevel(request.getVisibilityLevel());
        plan.setAiAnalysisLevel(request.getAiAnalysisLevel());


        return planToDto(planRepository.save(plan));


    }

    public PlanDto create(PlanRequest request) {

        log.info(request.toString());

        var plan = Plan.builder()
                .name(request.getName())
                .description(request.getDescription())
                .mostPopular(request.isMostPopular())
                .features(request.getFeatures())
                .maxBranches(request.getMaxBranches())
                .maxTablesPerBranch(request.getMaxTablesPerBranch())
                .visibilityLevel(request.getVisibilityLevel())
                .aiAnalysisLevel(request.getAiAnalysisLevel())
                .monthlyPrice(request.getMonthlyPrice()).build();

        plan = planRepository.save(plan);

        return planToDto(plan);

    }

    public List<AdminSubscriptionDto> getSubscriptions() {
        List<AdminSubscriptionDto> result = new ArrayList<>();
        for (Subscription sub : subscriptionRepository.findAll()) {
            String restaurantName = null;
            try {
                var res = restaurantClient.getRestaurantById(sub.getRestaurantId());
                restaurantName = res != null && res.getData() != null ? res.getData().getName() : null;
            } catch (Exception e) {
                log.warn("Restoran adı alına bilmədi (restaurantId={}): {}", sub.getRestaurantId(), e.getMessage());
            }

            result.add(AdminSubscriptionDto.builder()
                    .id(sub.getId())
                    .restaurantName(restaurantName)
                    .planName(sub.getPlan() != null ? sub.getPlan().getName() : null)
                    .startedAt(sub.getStartDate())
                    .currentPeriodEnd(sub.getEndDate())
                    .monthlyPrice(sub.getPlan() != null ? sub.getPlan().getMonthlyPrice() : null)
                    .status(sub.getStatus())
                    .build());
        }
        return result;
    }

    public AdminStatsDto getStats() {
        long restaurantCount = safeCount(restaurantClient::countRestaurants);
        long userCount = safeCount(authClient::countUsers);
        long monthlyReservations = safeCount(reservationClient::countThisMonth);
        long activeSubscriptions = subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE);

        BigDecimal monthlyRevenue = subscriptionRepository.findAll().stream()
                .filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE && s.getPlan() != null && s.getPlan().getMonthlyPrice() != null)
                .map(s -> s.getPlan().getMonthlyPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return AdminStatsDto.builder()
                .restaurantCount(restaurantCount)
                .activeSubscriptions(activeSubscriptions)
                .userCount(userCount)
                .monthlyReservations(monthlyReservations)
                .monthlyRevenue(monthlyRevenue)
                .build();
    }

    private long safeCount(java.util.function.Supplier<com.asanrezerv.subscriptionservice.dto.ApiResponse<Long>> call) {
        try {
            var res = call.get();
            return res != null && res.getData() != null ? res.getData() : 0L;
        } catch (Exception e) {
            log.warn("Statistika üçün servis çağırışı uğursuz oldu: {}", e.getMessage());
            return 0L;
        }
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
                .aiAnalysisLevel(plan.getAiAnalysisLevel())
                .build();

    }



}
