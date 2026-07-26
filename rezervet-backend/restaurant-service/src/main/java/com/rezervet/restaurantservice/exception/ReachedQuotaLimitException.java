package com.rezervet.restaurantservice.exception;

public class ReachedQuotaLimitException extends RuntimeException {
    public ReachedQuotaLimitException(String message) {
        super(message);
    }
}
