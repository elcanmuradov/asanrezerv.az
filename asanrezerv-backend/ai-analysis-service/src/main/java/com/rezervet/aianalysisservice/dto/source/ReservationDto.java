package com.rezervet.aianalysisservice.dto.source;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

// reservation-service ReservationDto-nun lokal kopyası (yalnız lazım olan sahələr)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    private UUID id;
    private String guestName;
    private Integer guestCount;
    private UUID guestId;
    private UUID restaurantId;
    private UUID branchId;
    private UUID tableId;
    private LocalDate date;
    private LocalTime startTime;
    private ReservationStatus status;
    private Source source;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
