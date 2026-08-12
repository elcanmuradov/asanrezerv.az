package com.rezervet.staffservice.mapper;

import com.rezervet.staffservice.dto.staff.StaffDto;
import com.rezervet.staffservice.entity.StaffAssignment;
import org.springframework.stereotype.Service;

@Service
public class StaffMapper {

    public StaffDto staffToDto(StaffAssignment assignment) {
        return StaffDto.builder()
                .id(assignment.getId())
                .userId(assignment.getUserId())
                .branchId(assignment.getBranchId())
                .fullName(assignment.getFullName())
                .email(assignment.getEmail())
                .role(assignment.getRole())
                .createdAt(assignment.getCreatedAt())
                .build();
    }

}
