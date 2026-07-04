package com.erasm.core.util;

public final class Constants {

    private Constants() {
        // Prevent instantiation
    }

    // Role Names
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_DELIVERY_MANAGER = "DELIVERY_MANAGER";
    public static final String ROLE_RESOURCE_MANAGER = "RESOURCE_MANAGER";
    public static final String ROLE_EMPLOYEE = "EMPLOYEE";
    public static final String ROLE_AUDITOR = "AUDITOR";

    // Security & JWT
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    public static final long JWT_EXPIRATION_MS = 86400000; // 24 hours

    // Validation & Business Rules
    public static final double MAX_ALLOCATION_PERCENTAGE = 100.0;
    public static final double MIN_ALLOCATION_PERCENTAGE = 0.0;
    public static final int MIN_EXPERIENCE_YEARS = 0;

    // Date Patterns
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // Response Messages
    public static final String MSG_SUCCESS = "Operation completed successfully";
    public static final String MSG_INTERNAL_SERVER_ERROR = "An unexpected error occurred on the server";
    public static final String MSG_UNAUTHORIZED = "Unauthorized access";
    public static final String MSG_FORBIDDEN = "Access denied";
}
