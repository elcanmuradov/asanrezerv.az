package com.rezervet.staffservice.service;

import com.rezervet.staffservice.client.AuthClient;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManagerService {
    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;
    private final AuthClient authClient;



    public List<StaffDto> getStaff(UUID userId) {
        List<StaffDto> staff = new ArrayList<>();
        staffRepository.findStaffAssignmentsByRole(StaffRole.WAITER).forEach(
                staffAssignment -> staff.add(staffMapper.staffToDto(staffAssignment))
        );


        return staff;

    }



    public StaffDto createWaiter(UUID userId, StaffRequest request) {

        WaiterAuthResponse response = authClient.createWaiter(request).getData();


        StaffAssignment assignment = StaffAssignment.builder()
                .userId(response.getId())
                .branchId(request.getBranchId())
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
