package com.rezervet.reservationservice.dto.request;

import com.rezervet.reservationservice.enums.Source;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {

    private String guestName;

    private Integer guestCount;

    private Integer duration; // (MINUTES)

    private UUID restaurantId;

    private UUID branchId;

    private UUID tableId;

    private LocalDate date;

    private LocalTime startTime;

}
