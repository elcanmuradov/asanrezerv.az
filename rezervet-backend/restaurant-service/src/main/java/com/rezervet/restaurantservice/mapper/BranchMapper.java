package com.rezervet.restaurantservice.mapper;

import com.rezervet.restaurantservice.dto.branch.BranchDto;
import com.rezervet.restaurantservice.entity.Branch;
import org.springframework.stereotype.Service;

@Service
public class BranchMapper {

    public BranchDto toBranchDto(Branch branch) {
        return BranchDto.builder()
                .id(branch.getId())
                .restaurantId(branch.getRestaurant().getId())
                .name(branch.getName())
                .address(branch.getAddress())
                .phone(branch.getPhone())
                .city(branch.getCity())
                .district(branch.getDistrict())
                .latitude(branch.getLatitude())
                .longitude(branch.getLongitude())
                .openingTime(branch.getOpeningTime())
                .closingTime(branch.getClosingTime())
                .createdAt(branch.getCreatedAt())
                .build();
    }

}
