package com.asanrezerv.restaurantservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    private String city;

    private String district;

    private Double latitude;

    private Double longitude;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime openingTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime closingTime;


    private String googleMapsLink;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "branch_photos", joinColumns = @JoinColumn(name = "branch_id"))
    @Column(name = "photo_url")
    @Builder.Default
    private List<String> photosUrl = new java.util.ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}