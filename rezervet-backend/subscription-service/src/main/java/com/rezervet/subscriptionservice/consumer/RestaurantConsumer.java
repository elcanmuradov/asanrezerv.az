package com.rezervet.subscriptionservice.consumer;

import com.rezervet.subscriptionservice.dto.kafka.BranchCreationEvent;
import com.rezervet.subscriptionservice.dto.kafka.RestaurantDto;
import com.rezervet.subscriptionservice.dto.kafka.TableCreationEvent;
import com.rezervet.subscriptionservice.entity.BranchQuotaUsage;
import com.rezervet.subscriptionservice.entity.RestaurantQuotaUsage;
import com.rezervet.subscriptionservice.entity.Subscription;
import com.rezervet.subscriptionservice.enums.SubscriptionStatus;
import com.rezervet.subscriptionservice.exception.NotFoundException;
import com.rezervet.subscriptionservice.repository.BranchUsageRepository;
import com.rezervet.subscriptionservice.repository.RestaurantUsageRepository;
import com.rezervet.subscriptionservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantConsumer {
    private final BranchUsageRepository branchUsageRepository;
    private final RestaurantUsageRepository restaurantUsageRepository;
    private final SubscriptionRepository subscriptionRepository;

    @KafkaListener(topics = "restaurant.created",containerFactory = "restaurant")
    public void createRestaurantSubs(RestaurantDto dto) {
        var usage = RestaurantQuotaUsage.builder().restaurantId(dto.getId()).currentBranchCount(0).build();

        restaurantUsageRepository.save(usage);
    }


    @KafkaListener(topics = "table.deleted",containerFactory = "uuid")
    public void tableDeleted(UUID branchId) {
        var opt= branchUsageRepository.findBranchQuotaUsageByBranchId(branchId);
        if(opt.isEmpty()){
            throw new NotFoundException("No such branch");
        }

        var usage = opt.get();

        usage.setCurrentTableCount(usage.getCurrentTableCount() - 1);
        branchUsageRepository.save(usage);

    }

    @KafkaListener(topics = "branch.deleted",containerFactory = "restaurant")
    public void branchDeleted(RestaurantDto dto) {
        var opt= restaurantUsageRepository.findRestaurantQuotaUsageByRestaurantId(dto.getId());
        if(opt.isEmpty()){
            throw new NotFoundException("No such branch");
        }

        var usage = opt.get();

        usage.setCurrentBranchCount(usage.getCurrentBranchCount() - 1);
        restaurantUsageRepository.save(usage);

    }


    @KafkaListener(topics = "branch.created",containerFactory = "branch")
    public void branchCreated(BranchCreationEvent event) {
       var otp =  restaurantUsageRepository.findRestaurantQuotaUsageByRestaurantId(event.getRestaurantId());

       if (otp.isEmpty()) {
           throw new NotFoundException("Restaurant not found");
       }

       var usage = otp.get();

       usage.setCurrentBranchCount(usage.getCurrentBranchCount() + 1);
       restaurantUsageRepository.save(usage);

       var branchUsage = BranchQuotaUsage.builder().branchId(event.getBranchId()).restaurantId(event.getRestaurantId()).currentTableCount(0).build();
       branchUsageRepository.save(branchUsage);

    }

    @KafkaListener(topics = "table.created",containerFactory = "table")
    public void tableCreated(TableCreationEvent event) {
        var otp = branchUsageRepository.findBranchQuotaUsageByBranchId(event.getBranchId());
        if (otp.isEmpty()) {
            throw new NotFoundException("Branch not found");
        }
        var usage = otp.get();

        usage.setCurrentTableCount(usage.getCurrentTableCount() + 1);
        branchUsageRepository.save(usage);
    }


}
