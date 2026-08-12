package com.asanrezerv.subscriptionservice.consumer;

import com.asanrezerv.subscriptionservice.dto.kafka.BranchCreationEvent;
import com.asanrezerv.subscriptionservice.dto.kafka.RestaurantDto;
import com.asanrezerv.subscriptionservice.dto.kafka.TableCreationEvent;
import com.asanrezerv.subscriptionservice.entity.BranchQuotaUsage;
import com.asanrezerv.subscriptionservice.entity.RestaurantQuotaUsage;
import com.asanrezerv.subscriptionservice.exception.NotFoundException;
import com.asanrezerv.subscriptionservice.repository.BranchUsageRepository;
import com.asanrezerv.subscriptionservice.repository.RestaurantUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantConsumer {
    private final BranchUsageRepository branchUsageRepository;
    private final RestaurantUsageRepository restaurantUsageRepository;

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

    // restaurant-service bu topic-ə RestaurantDto YOX, restoranın raw UUID-sini göndərir.
    @KafkaListener(topics = "branch.deleted",containerFactory = "uuid")
    public void branchDeleted(UUID restaurantId) {
        var opt= restaurantUsageRepository.findRestaurantQuotaUsageByRestaurantId(restaurantId);
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

       var branchUsage = BranchQuotaUsage.builder()
               .branchId(event.getBranchId())
               .restaurantId(event.getRestaurantId())
               .currentTableCount(0)
               .build();
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
