package com.erasm.core.util;

import java.util.regex.Pattern;

public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private ValidationUtil() {
        // Prevent instantiation
    }

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        // At least 8 characters, at least one letter and one number
        return password.length() >= 8 && password.matches(".*[a-zA-Z].*") && password.matches(".*\\d.*");
    }

    public static boolean isValidAllocationPercentage(Double percentage) {
        if (percentage == null) {
            return false;
        }
        return percentage >= Constants.MIN_ALLOCATION_PERCENTAGE && percentage <= Constants.MAX_ALLOCATION_PERCENTAGE;
    }
}
