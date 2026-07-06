package com.erasm.core.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;

public enum ProjectStatus {
    PLANNING,
    PLANNED,
    IN_PROGRESS,
    ON_HOLD,
    COMPLETED,
    CANCELLED;

    @JsonCreator
    public static ProjectStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (ProjectStatus status : ProjectStatus.values()) {
            if (status.name().equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid project status: " + value + ". Allowed values: " + 
            Arrays.toString(ProjectStatus.values()));
    }
}
