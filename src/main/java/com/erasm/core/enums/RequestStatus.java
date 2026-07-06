package com.erasm.core.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;

public enum RequestStatus {
    DRAFT,
    SUBMITTED,
    RESOURCE_MANAGER_REVIEW,
    APPROVED,
    ALLOCATED,
    REJECTED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    @JsonCreator
    public static RequestStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (RequestStatus status : RequestStatus.values()) {
            if (status.name().equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid request status: " + value + ". Allowed values: " + 
            Arrays.toString(RequestStatus.values()));
    }
}
