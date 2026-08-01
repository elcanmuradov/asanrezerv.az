package com.rezervet.reservationservice.repository;

import com.rezervet.reservationservice.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {


    @Query("""
            SELECT r FROM Reservation r
            WHERE r.branchId = :branchId AND r.date = :date AND r.startTime < :end AND r.endTime > :start AND (r.status = 0 OR r.status = 1)
            """)
    List<Reservation> findReservations(@Param("branchId") UUID branchId,@Param("date") LocalDate date, @Param("start") LocalTime start,@Param("end") LocalTime end);

    List<Reservation> findReservationsByGuestId(UUID guestId);

    List<Reservation> findReservationsByRestaurantId(UUID branchId);

    List<Reservation> findReservationsByBranchId(UUID branchId);

    long countByDateBetween(LocalDate from, LocalDate to);
}
/*
    12:00 - 14:00 id 1

    13:00 - 15:00

    startTime > endTime
    startTune < startTime


@Query("""
        SELECT r FROM Restaurant r
        WHERE (:search IS NULL
               OR LOWER(r.name) LIKE :search
               OR LOWER(r.cuisine) LIKE :search)
          AND (:city IS NULL OR LOWER(r.city) = :city)
        """)
    Page<Restaurant> search(@Param("search") String search,
                            @Param("city") String city,
                            Pageable pageable);

 */