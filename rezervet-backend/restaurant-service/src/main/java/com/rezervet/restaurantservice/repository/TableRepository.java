package com.rezervet.restaurantservice.repository;

import com.rezervet.restaurantservice.entity.Branch;
import com.rezervet.restaurantservice.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TableRepository extends JpaRepository<RestaurantTable, UUID> {

    List<RestaurantTable> findRestaurantTablesByBranch(Branch branch);


}
