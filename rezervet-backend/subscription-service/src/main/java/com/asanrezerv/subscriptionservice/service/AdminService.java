package com.asanrezerv.subscriptionservice.service;

import com.asanrezerv.subscriptionservice.dto.controller.PlanDto;
import com.asanrezerv.subscriptionservice.dto.controller.PlanRequest;
import com.asanrezerv.subscriptionservice.entity.Plan;
import com.asanrezerv.subscriptionservice.exception.NotFoundException;
import com.asanrezerv.subscriptionservice.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final PlanRepository planRepository;

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
                .monthlyPrice(request.getMonthlyPrice()).build();

        plan = planRepository.save(plan);

        return planToDto(plan);

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



}
