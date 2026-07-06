package com.erasm.core.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;

public enum AllocationStatus {
    ALLOCATED,
    ACTIVE,
    COMPLETED,
    RELEASED;

    @JsonCreator
    public static AllocationStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (AllocationStatus status : AllocationStatus.values()) {
            if (status.name().equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid allocation status: " + value + ". Allowed values: " + 
            Arrays.toString(AllocationStatus.values()));
    }
}
