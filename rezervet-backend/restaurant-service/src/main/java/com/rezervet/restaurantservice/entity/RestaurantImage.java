package com.rezervet.restaurantservice.entity;

import com.rezervet.restaurantservice.enums.PhotoType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "product_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "public_id")
    private String publicId;

    @Enumerated(EnumType.ORDINAL)
    private PhotoType mediaType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}