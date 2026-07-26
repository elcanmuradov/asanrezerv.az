package com.rezervet.staffservice.repository;

import com.rezervet.staffservice.entity.StaffAssignment;
import com.rezervet.staffservice.enums.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StaffRepository extends JpaRepository<StaffAssignment, UUID> {
    Optional<StaffAssignment> findStaffAssignmentByUserId(UUID userId);

    List<StaffAssignment> findStaffAssignmentsByRole(StaffRole role);
}
