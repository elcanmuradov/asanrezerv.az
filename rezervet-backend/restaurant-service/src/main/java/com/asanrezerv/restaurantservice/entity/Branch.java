package com.asanrezerv.restaurantservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "branches")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Eyni servisdə olduğu üçün əlaqə istifadə edirik.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private String name;

    private String address;
    private String phone;

    // search-service-də BranchDocument üçün (şəhər üzrə filtr, coğrafi axtarış)
    private String city;
    private String district;
    private Double latitude;
    private Double longitude;

    private Time openingTime;
    private Time closingTime;


    private String googleMapsLink;

    private List<String> photosUrl;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}