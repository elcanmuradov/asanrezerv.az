package com.rezervet.restaurantservice.repository;

import com.rezervet.restaurantservice.entity.Branch;
import com.rezervet.restaurantservice.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {

    List<Branch> findBranchesByRestaurant(Restaurant restaurant);

    List<Branch> findBranchesBy(String branchCode);

    long countByRestaurant(Restaurant restaurant);

}
