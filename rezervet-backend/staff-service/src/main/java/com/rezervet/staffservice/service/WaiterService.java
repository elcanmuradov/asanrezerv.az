package com.rezervet.staffservice.service;

import com.rezervet.staffservice.client.RestaurantClient;
import com.rezervet.staffservice.enums.TableStatus;
import com.rezervet.staffservice.exception.NotFoundException;
import com.rezervet.staffservice.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaiterService {
    private final StaffRepository staffRepository;
    private final RestaurantClient restaurantClient;

    public UUID getMyBranches(UUID userId) {

        var opt = staffRepository.findStaffAssignmentByUserId(userId);
        if (opt.isEmpty()) {
            throw new NotFoundException("User not found");
        }

       return opt.get().getBranchId();



    }

    public void changeTableStatus(UUID userId, UUID tableId, TableStatus status) {
        restaurantClient.changeStatus(status,userId);
    }




}


