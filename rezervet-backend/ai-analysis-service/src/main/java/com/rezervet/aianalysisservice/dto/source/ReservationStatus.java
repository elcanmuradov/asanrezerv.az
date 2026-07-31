package com.rezervet.aianalysisservice.dto.source;

// reservation-service ilə EYNİ ad/sıra (JSON adı ilə deserialize olunur)
public enum ReservationStatus {
    PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW
}
