package com.rezervet.restaurantservice.repository;

import com.rezervet.restaurantservice.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    Optional<Restaurant> findRestaurantById(UUID id);

    List<Restaurant> findRestaurantsByCity(String city);

    Optional<Restaurant> findRestaurantByOwnerId(UUID ownerId);

    @Query("""
        SELECT r FROM Restaurant r
        WHERE (:search IS NULL
               OR LOWER(r.name) LIKE :search
               OR LOWER(r.cuisine) LIKE :search)
          AND (:city IS NULL OR LOWER(r.city) = :city)
        """)
    Page<Restaurant> search(@Param("search") String search,
                            @Param("city") String city,
                            Pageable pageable);

}
