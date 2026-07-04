package com.erasm.core.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DateUtil}.
 * Covers all formatting, parsing, and overlap logic including branch conditions.
 */
public class DateUtilTest {

    // ========================= format(LocalDate) =========================

    @Test
    void testFormatLocalDate_ValidDate() {
        LocalDate date = LocalDate.of(2026, 6, 30);
        String formatted = DateUtil.format(date);
        assertNotNull(formatted);
        assertFalse(formatted.isEmpty());
    }

    @Test
    void testFormatLocalDate_Null() {
        String result = DateUtil.format((LocalDate) null);
        assertNull(result);
    }

    // ========================= format(LocalDateTime) =========================

    @Test
    void testFormatLocalDateTime_ValidDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 6, 30, 10, 30, 0);
        String formatted = DateUtil.format(dateTime);
        assertNotNull(formatted);
        assertFalse(formatted.isEmpty());
    }

    @Test
    void testFormatLocalDateTime_Null() {
        String result = DateUtil.format((LocalDateTime) null);
        assertNull(result);
    }

    // ========================= parseDate =========================

    @Test
    void testParseDate_ValidString() {
        String formatted = DateUtil.format(LocalDate.of(2026, 6, 30));
        LocalDate parsed = DateUtil.parseDate(formatted);
        assertNotNull(parsed);
        assertEquals(2026, parsed.getYear());
        assertEquals(6, parsed.getMonthValue());
        assertEquals(30, parsed.getDayOfMonth());
    }

    @Test
    void testParseDate_Null() {
        LocalDate result = DateUtil.parseDate(null);
        assertNull(result);
    }

    // ========================= parseDateTime =========================

    @Test
    void testParseDateTime_ValidString() {
        LocalDateTime original = LocalDateTime.of(2026, 6, 30, 14, 0, 0);
        String formatted = DateUtil.format(original);
        LocalDateTime parsed = DateUtil.parseDateTime(formatted);
        assertNotNull(parsed);
        assertEquals(2026, parsed.getYear());
    }

    @Test
    void testParseDateTime_Null() {
        LocalDateTime result = DateUtil.parseDateTime(null);
        assertNull(result);
    }

    // ========================= isOverlap — all branches =========================

    @Test
    void testIsOverlap_Overlapping() {
        // [Jan 1 - Jun 30] overlaps [Apr 1 - Dec 31]
        assertTrue(DateUtil.isOverlap(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30),
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 12, 31)));
    }

    @Test
    void testIsOverlap_NonOverlapping() {
        // [Jan 1 - Mar 31] does NOT overlap [Apr 1 - Dec 31]
        assertFalse(DateUtil.isOverlap(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31),
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 12, 31)));
    }

    @Test
    void testIsOverlap_NullStartA() {
        // null startA → no overlap
        assertFalse(DateUtil.isOverlap(
                null, LocalDate.of(2026, 6, 30),
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 12, 31)));
    }

    @Test
    void testIsOverlap_NullStartB() {
        // null startB → no overlap
        assertFalse(DateUtil.isOverlap(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30),
                null, LocalDate.of(2026, 12, 31)));
    }

    @Test
    void testIsOverlap_NullEndA_OpenEnded() {
        // endA is null (open-ended), so it extends to LocalDate.MAX
        assertTrue(DateUtil.isOverlap(
                LocalDate.of(2026, 1, 1), null,
                LocalDate.of(2027, 1, 1), LocalDate.of(2027, 12, 31)));
    }

    @Test
    void testIsOverlap_NullEndB_OpenEnded() {
        // endB is null (open-ended)
        assertTrue(DateUtil.isOverlap(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31),
                LocalDate.of(2026, 6, 1), null));
    }

    @Test
    void testIsOverlap_SameDay() {
        // Both ranges share a single day
        assertTrue(DateUtil.isOverlap(
                LocalDate.of(2026, 6, 15), LocalDate.of(2026, 6, 15),
                LocalDate.of(2026, 6, 15), LocalDate.of(2026, 6, 15)));
    }

    @Test
    void testIsOverlap_AdjacentRanges() {
        // [Jan 1 - Mar 31] and [Apr 1 - Jun 30] are adjacent — no overlap
        assertFalse(DateUtil.isOverlap(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31),
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 6, 30)));
    }

    @Test
    void testIsOverlap_BothNull_NoOverlap() {
        assertFalse(DateUtil.isOverlap(null, null, null, null));
    }
}
