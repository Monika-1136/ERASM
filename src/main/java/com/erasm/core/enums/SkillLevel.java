package com.erasm.core.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;

public enum SkillLevel {
    FRESHER,
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    EXPERT;

    @JsonCreator
    public static SkillLevel fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (SkillLevel level : SkillLevel.values()) {
            if (level.name().equalsIgnoreCase(value.trim())) {
                return level;
            }
        }
        throw new IllegalArgumentException("Invalid skill level: " + value + ". Allowed values: " + 
            Arrays.toString(SkillLevel.values()));
    }
}
