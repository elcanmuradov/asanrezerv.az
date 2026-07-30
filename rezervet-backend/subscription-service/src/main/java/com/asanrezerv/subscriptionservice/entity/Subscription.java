package com.asanrezerv.subscriptionservice.entity;

import com.asanrezerv.subscriptionservice.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID ownerId;

    private UUID restaurantId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Plan plan;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean autoRenew;

    private SubscriptionStatus status;

}


