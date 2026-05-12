package com.kairos.catalog.security;

/**
 * Central definition of application roles.
 *
 * NOTE:
 * - Do NOT prefix with ROLE_
 * - Spring Security adds ROLE_ automatically
 */
public final class Roles {

    public static final String USER = "USER";
    public static final String ADMIN = "ADMIN";

    private Roles() {
        // utility class
    }
}