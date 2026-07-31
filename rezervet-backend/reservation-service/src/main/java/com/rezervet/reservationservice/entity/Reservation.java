package com.rezervet.reservationservice.entity;

import com.rezervet.reservationservice.enums.ReservationStatus;
import com.rezervet.reservationservice.enums.Source;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String guestName;

    private String guestPhone;

    private UUID guestId;

    private Integer guestCount;

    private String note;

    private UUID restaurantId;

    private UUID branchId;

    private UUID tableId;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    @Enumerated(EnumType.ORDINAL)
    private ReservationStatus status;

    @Enumerated(EnumType.ORDINAL)
    private Source source;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


}
/*

- **Rol:** Rezervasiya yaratma/təsdiq/ləğv, vaxt slotları, ikiqat-rezerv qarşısı (concurrency), no-show idarəsi, **waiter-in əl ilə daxil etdiyi** (offline/zəng) rezervlər.
- **Tech:** Spring Boot Web, Spring Data JPA, **Redis** (slot lock — distributed lock, Redisson və ya `SET NX`), Bean Validation.
- **Concurrency:** Eyni masa/slot üçün optimistic locking (`@Version`) + Redis lock → race condition olmasın.
- **DB:** PostgreSQL. Entity: `Reservation` (userId?/guestInfo, filialId, tableId, timeSlot, status, source[ONLINE|MANUAL]), `TimeSlot`.
- **Event:** `ReservationCreated/Confirmed/Cancelled/NoShow` → Kafka (Notification, Analytics, Table-Status).
- **Görünmə prioriteti:** Restoran listing sorğusu paket səviyyəsinə görə sıralanır (Premium > Standart > Starter) — sadə SQL ranking, sonra Elasticsearch (opsional).


 */