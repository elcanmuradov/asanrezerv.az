package com.asanrezerv.subscriptionservice.enums;

// DİQQƏT: entity-də @Enumerated yoxdur -> JPA defolt olaraq ORDINAL saxlayır.
// Sırayı DƏYİŞMƏ, yeni status yalnız SONA əlavə olunmalıdır.
public enum SubscriptionStatus {
    ACTIVE, PAST_DUE, CANCELLED, PENDING
}
