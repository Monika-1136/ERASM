package com.erasm.core.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;

public enum RoleName {
    ROLE_ADMIN,
    ROLE_DELIVERY_MANAGER,
    ROLE_RESOURCE_MANAGER,
    ROLE_EMPLOYEE,
    ROLE_AUDITOR;

    @JsonCreator
    public static RoleName fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String cleaned = value.trim().toUpperCase();
        if (!cleaned.startsWith("ROLE_")) {
            cleaned = "ROLE_" + cleaned;
        }
        for (RoleName rn : RoleName.values()) {
            if (rn.name().equals(cleaned)) {
                return rn;
            }
        }
        throw new IllegalArgumentException("Invalid role name: " + value + ". Allowed values: " + 
            Arrays.toString(RoleName.values()));
    }
}
