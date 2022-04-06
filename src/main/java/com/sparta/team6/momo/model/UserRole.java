package com.sparta.team6.momo.model;

import lombok.Getter;

public enum UserRole {
    ROLE_USER(Authority.USER),
    ROLE_GUEST(Authority.GUEST);

    @Getter
    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String GUEST = "ROLE_GUEST";
    }

}
