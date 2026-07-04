package com.erasm.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AppUtil}.
 * Covers getCurrentUserEmail, generateRandomPassword, and isNullOrEmpty.
 */
@ExtendWith(MockitoExtension.class)
public class AppUtilTest {

    // ========================= getCurrentUserEmail =========================

    @Test
    void testGetCurrentUserEmail_WithUserDetails() {
        UserDetails userDetails = new User("alice@erasm.com", "password", Collections.emptyList());

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        String email = AppUtil.getCurrentUserEmail();
        assertEquals("alice@erasm.com", email);

        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetCurrentUserEmail_WithStringPrincipal() {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn("bob@erasm.com");
        when(auth.getName()).thenReturn("bob@erasm.com");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        String email = AppUtil.getCurrentUserEmail();
        assertEquals("bob@erasm.com", email);

        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetCurrentUserEmail_NullAuthentication() {
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(context);

        String email = AppUtil.getCurrentUserEmail();
        assertEquals("anonymousUser", email);

        SecurityContextHolder.clearContext();
    }

    // ========================= generateRandomPassword =========================

    @Test
    void testGenerateRandomPassword_ValidLength() {
        String password = AppUtil.generateRandomPassword(12);
        assertNotNull(password);
        assertEquals(12, password.length());
    }

    @Test
    void testGenerateRandomPassword_MinimumLength() {
        String password = AppUtil.generateRandomPassword(4);
        assertNotNull(password);
        assertEquals(4, password.length());
    }

    @Test
    void testGenerateRandomPassword_LargeLength() {
        String password = AppUtil.generateRandomPassword(50);
        assertNotNull(password);
        assertEquals(50, password.length());
    }

    @Test
    void testGenerateRandomPassword_TooShort_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> AppUtil.generateRandomPassword(3));
    }

    @Test
    void testGenerateRandomPassword_LengthZero_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> AppUtil.generateRandomPassword(0));
    }

    // ========================= isNullOrEmpty =========================

    @Test
    void testIsNullOrEmpty_NullString() {
        assertTrue(AppUtil.isNullOrEmpty(null));
    }

    @Test
    void testIsNullOrEmpty_EmptyString() {
        assertTrue(AppUtil.isNullOrEmpty(""));
    }

    @Test
    void testIsNullOrEmpty_WhitespaceString() {
        assertTrue(AppUtil.isNullOrEmpty("   "));
    }

    @Test
    void testIsNullOrEmpty_ValidString() {
        assertFalse(AppUtil.isNullOrEmpty("hello"));
    }

    @Test
    void testIsNullOrEmpty_SingleCharacter() {
        assertFalse(AppUtil.isNullOrEmpty("a"));
    }
}
