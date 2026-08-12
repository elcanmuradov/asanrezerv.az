package com.asanrezerv.subscriptionservice.service;

import com.asanrezerv.subscriptionservice.dto.kafka.RestaurantDto;
import com.asanrezerv.subscriptionservice.entity.Subscription;
import com.asanrezerv.subscriptionservice.enums.SubscriptionStatus;
import com.asanrezerv.subscriptionservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class ScheduledService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SubscriptionRepository subscriptionRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void checkEndDate() {
        log.info("Abunəlik vaxtı bitmiş restoranlar yoxlanılır...");

        LocalDate now = LocalDate.now();

        List<Subscription> subscriptions = subscriptionRepository.findSubscriptionsByStatusAndEndDateBefore(SubscriptionStatus.ACTIVE,now);


        for (Subscription subscription : subscriptions) {
            try {
                handleExpiredSubscription(subscription);
            } catch (Exception e) {
                log.error("Abunəlik bitmə prosesi xətası: {}", subscription.getId(), e);
            }
        }

        log.info("{} abunəlik emal edildi", subscriptions.size());


    }

    private void handleExpiredSubscription(Subscription subscription) {
        if (subscription.getAutoRenew()) {
            // odenis sistemi
            subscription.setEndDate(LocalDate.now().plusDays(30));
            subscriptionRepository.save(subscription);
        }else{
            subscription.setStatus(SubscriptionStatus.PAST_DUE);
            subscriptionRepository.save(subscription);
            var event = new RestaurantDto(subscription.getRestaurantId());
            kafkaTemplate.send("restaurant.subscription.expired",event);
        }
    }

}
