package com.rezervet.restaurantservice.repository;


import com.rezervet.restaurantservice.entity.RestaurantImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RestaurantImageRepository extends JpaRepository<RestaurantImage, UUID> {


}
