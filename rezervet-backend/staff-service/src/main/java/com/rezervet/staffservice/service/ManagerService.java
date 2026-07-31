package com.rezervet.staffservice.service;

import com.rezervet.staffservice.client.AuthClient;
import com.rezervet.staffservice.client.RestaurantClient;
import com.rezervet.staffservice.dto.restaurant.BranchDto;
import com.rezervet.staffservice.dto.staff.StaffDto;
import com.rezervet.staffservice.dto.staff.StaffRequest;
import com.rezervet.staffservice.dto.staff.WaiterAuthResponse;
import com.rezervet.staffservice.entity.StaffAssignment;
import com.rezervet.staffservice.enums.StaffRole;
import com.rezervet.staffservice.exception.NotFoundException;
import com.rezervet.staffservice.mapper.StaffMapper;
import com.rezervet.staffservice.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerService {
    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;
    private final AuthClient authClient;
    private final RestaurantClient restaurantClient;



    public List<StaffDto> getStaff(UUID userId) {
        UUID restaurantId = restaurantClient.getMyRestaurant(userId).getData().getId();
        List<BranchDto> branches = restaurantClient.getBranchesByRestaurant(restaurantId).getData();
        List<UUID> branchIds = branches.stream().map(BranchDto::getId).toList();
        Map<UUID, String> branchNames = branches.stream()
                .collect(Collectors.toMap(BranchDto::getId, BranchDto::getName));

        return staffRepository.findStaffAssignmentsByRoleAndBranchIdIn(StaffRole.WAITER, branchIds).stream()
                .map(assignment -> {
                    StaffDto dto = staffMapper.staffToDto(assignment);
                    dto.setBranch(StaffDto.BranchSummary.builder()
                            .id(assignment.getBranchId())
                            .name(branchNames.get(assignment.getBranchId()))
                            .build());
                    return dto;
                })
                .toList();
    }



    public StaffDto createWaiter(UUID userId, StaffRequest request) {

        WaiterAuthResponse response = authClient.createWaiter(request).getData();


        StaffAssignment assignment = StaffAssignment.builder()
                .userId(response.getId())
                .branchId(request.getBranchId())
                .fullName(response.getFullName())
                .email(response.getEmail())
                .role(StaffRole.WAITER)
                .build();

        assignment = staffRepository.save(assignment);

        return staffMapper.staffToDto(assignment);


    }

    public void deleteWaiter(UUID userId, UUID id) {
        var opt = staffRepository.findById(id);
        if (opt.isEmpty()) {
            throw new NotFoundException("Işçi tapılmadı!");
        }

        staffRepository.deleteById(id);

    }


}
