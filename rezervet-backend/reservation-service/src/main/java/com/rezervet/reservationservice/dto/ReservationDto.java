package com.rezervet.reservationservice.dto;

import com.rezervet.reservationservice.enums.ReservationStatus;
import com.rezervet.reservationservice.enums.Source;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReservationDto {
    private UUID id;

    private String guestName;

    private String guestPhone;

    private Integer guestCount;

    private String note;

    private UUID guestId;

    private UUID restaurantId;

    private UUID branchId;

    private String branchName;

    private UUID tableId;

    private String tableName;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private ReservationStatus status;

    private Source source;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
