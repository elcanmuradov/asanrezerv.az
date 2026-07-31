package com.rezervet.staffservice.entity;

import com.rezervet.staffservice.enums.StaffRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "staff_assignments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "branch_id", "role"})
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // auth-service-dəki User.id (menecer və ya ofisiant). FK YOX.
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    private UUID branchId;

    // auth-service-dən yaradılış anında kopyalanır (hər dəfə join etməmək üçün).
    private String fullName;

    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StaffRole role;

    @CreationTimestamp
    private LocalDateTime createdAt;
}