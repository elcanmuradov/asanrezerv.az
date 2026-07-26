package com.rezervet.authservice.utils.enums;

public enum AccountType {
    USER(Role.USER),
    MANAGER(Role.MANAGER);

    private final Role role;
    AccountType(Role role) { this.role = role; }
    public Role toRole() { return role; }
}