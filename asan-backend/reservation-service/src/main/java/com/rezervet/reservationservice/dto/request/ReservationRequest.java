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

    private String guestPhone;

    private Integer guestCount;

    private String note;

    private Integer duration; // (MINUTES)

    private UUID restaurantId;

    private UUID branchId;

    private UUID tableId; // Əl ilə rezervdə menecer/ofisiant konkret masa seçə bilər (opsional)

    private LocalDate date;

    private LocalTime startTime;

}
