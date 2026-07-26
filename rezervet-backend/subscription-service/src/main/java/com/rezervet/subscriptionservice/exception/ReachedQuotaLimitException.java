package com.rezervet.subscriptionservice.exception;

public class ReachedQuotaLimitException extends RuntimeException {
    public ReachedQuotaLimitException(String message) {
        super(message);
    }
}
