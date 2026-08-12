package com.asanrezerv.subscriptionservice.exception;

public class ReachedQuotaLimitException extends RuntimeException {
    public ReachedQuotaLimitException(String message) {
        super(message);
    }
}
