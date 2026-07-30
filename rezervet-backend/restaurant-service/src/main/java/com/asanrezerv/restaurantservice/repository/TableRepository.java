package com.asanrezerv.restaurantservice.repository;

import com.asanrezerv.restaurantservice.entity.Branch;
import com.asanrezerv.restaurantservice.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TableRepository extends JpaRepository<RestaurantTable, UUID> {

    List<RestaurantTable> findRestaurantTablesByBranch(Branch branch);


}
