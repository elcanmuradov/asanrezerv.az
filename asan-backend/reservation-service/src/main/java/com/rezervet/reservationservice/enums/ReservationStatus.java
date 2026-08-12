package com.rezervet.reservationservice.enums;


// DİQQƏT: entity-də @Enumerated(EnumType.ORDINAL) istifadə olunur — sıranı dəyişmə,
// yalnız sona yeni status əlavə et (əks halda mövcud DB sətirlərinin statusu dəyişər).
public enum ReservationStatus {

    PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW

}
