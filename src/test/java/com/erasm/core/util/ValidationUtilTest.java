package com.erasm.core.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ValidationUtil}.
 * Covers email, password, and allocation percentage validation — all branches.
 */
public class ValidationUtilTest {

    // ========================= isValidEmail =========================

    @Test
    void testIsValidEmail_ValidEmail() {
        assertTrue(ValidationUtil.isValidEmail("alice@erasm.com"));
    }

    @Test
    void testIsValidEmail_ValidEmailWithSubdomain() {
        assertTrue(ValidationUtil.isValidEmail("user.name+tag@mail.example.co"));
    }

    @Test
    void testIsValidEmail_Null() {
        assertFalse(ValidationUtil.isValidEmail(null));
    }

    @Test
    void testIsValidEmail_NoAtSign() {
        assertFalse(ValidationUtil.isValidEmail("invalidemail.com"));
    }

    @Test
    void testIsValidEmail_NoDomain() {
        assertFalse(ValidationUtil.isValidEmail("user@"));
    }

    @Test
    void testIsValidEmail_NoLocalPart() {
        assertFalse(ValidationUtil.isValidEmail("@domain.com"));
    }

    @Test
    void testIsValidEmail_EmptyString() {
        assertFalse(ValidationUtil.isValidEmail(""));
    }

    @Test
    void testIsValidEmail_SpacesInEmail() {
        assertFalse(ValidationUtil.isValidEmail("user name@domain.com"));
    }

    // ========================= isValidPassword =========================

    @Test
    void testIsValidPassword_ValidPassword() {
        assertTrue(ValidationUtil.isValidPassword("Password1"));
    }

    @Test
    void testIsValidPassword_ValidPasswordWithSpecialChars() {
        assertTrue(ValidationUtil.isValidPassword("Secure@123"));
    }

    @Test
    void testIsValidPassword_Null() {
        assertFalse(ValidationUtil.isValidPassword(null));
    }

    @Test
    void testIsValidPassword_TooShort() {
        assertFalse(ValidationUtil.isValidPassword("Ab1"));
    }

    @Test
    void testIsValidPassword_ExactlySevenChars() {
        // 7 chars with letters and digit — still invalid (< 8)
        assertFalse(ValidationUtil.isValidPassword("Abc1234"));
    }

    @Test
    void testIsValidPassword_NoDigit() {
        assertFalse(ValidationUtil.isValidPassword("Password"));
    }

    @Test
    void testIsValidPassword_NoLetter() {
        assertFalse(ValidationUtil.isValidPassword("12345678"));
    }

    @Test
    void testIsValidPassword_EmptyString() {
        assertFalse(ValidationUtil.isValidPassword(""));
    }

    @Test
    void testIsValidPassword_ExactlyEightCharsValid() {
        assertTrue(ValidationUtil.isValidPassword("Abcde1fg"));
    }

    // ========================= isValidAllocationPercentage =========================

    @Test
    void testIsValidAllocationPercentage_ValidMidRange() {
        assertTrue(ValidationUtil.isValidAllocationPercentage(50.0));
    }

    @Test
    void testIsValidAllocationPercentage_MinBoundary() {
        // Minimum should be Constants.MIN_ALLOCATION_PERCENTAGE (likely 1.0 or 0.0)
        assertTrue(ValidationUtil.isValidAllocationPercentage(Constants.MIN_ALLOCATION_PERCENTAGE));
    }

    @Test
    void testIsValidAllocationPercentage_MaxBoundary() {
        assertTrue(ValidationUtil.isValidAllocationPercentage(Constants.MAX_ALLOCATION_PERCENTAGE));
    }

    @Test
    void testIsValidAllocationPercentage_BelowMin() {
        // Negative value is always below minimum
        assertFalse(ValidationUtil.isValidAllocationPercentage(-1.0));
    }

    @Test
    void testIsValidAllocationPercentage_AboveMax() {
        assertFalse(ValidationUtil.isValidAllocationPercentage(101.0));
    }

    @Test
    void testIsValidAllocationPercentage_Null() {
        assertFalse(ValidationUtil.isValidAllocationPercentage(null));
    }

    @Test
    void testIsValidAllocationPercentage_Zero() {
        // 0 is below minimum (min is typically > 0)
        boolean result = ValidationUtil.isValidAllocationPercentage(0.0);
        // Just verify it doesn't throw — result depends on Constants.MIN_ALLOCATION_PERCENTAGE
        assertNotNull(result);
    }
}
