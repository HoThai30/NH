package com.example.demo.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Small helpers to ease a migration from LocalDateTime (no zone) to UTC-based storage.
 * - Treats existing LocalDateTime values as UTC when converting to Instant/OffsetDateTime.
 * - Provides formatting helpers for ISO/UTC output.
 */
public final class DateTimeUtil {
    private static final ZoneOffset UTC = ZoneOffset.UTC;
    private static final DateTimeFormatter ISO_OFFSET = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private DateTimeUtil() {}

    public static Instant toInstantUTC(LocalDateTime ldt) {
        if (ldt == null) return null;
        return ldt.atOffset(UTC).toInstant();
    }

    public static LocalDateTime fromInstantUTC(Instant instant) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, UTC);
    }

    public static OffsetDateTime toOffsetDateTimeUTC(LocalDateTime ldt) {
        if (ldt == null) return null;
        return ldt.atOffset(UTC);
    }

    public static String formatIsoUtc(LocalDateTime ldt) {
        if (ldt == null) return null;
        return toOffsetDateTimeUTC(ldt).format(ISO_OFFSET);
    }

    public static String formatInstantIsoUtc(Instant instant) {
        if (instant == null) return null;
        return OffsetDateTime.ofInstant(instant, UTC).format(ISO_OFFSET);
    }
}
