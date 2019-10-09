package com.bravson.socialalert.infrastructure.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;

public interface DateUtil {
	
	final DateTimeFormatter COMPACT_DATE_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .parseStrict()
            .appendPattern("yyyyMMdd")
            .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
            .toFormatter()
            .withZone(ZoneOffset.UTC);

	static Instant toInstant(Date date) {
		return date != null ? date.toInstant() : null;
	}
	
	static Date toDate(Instant instant) {
		return instant != null ? Date.from(instant) : null;
	}
	
	static Instant parseInstant(String value, DateTimeFormatter formatter) {
		return value != null ? formatter.parse(value, Instant::from) : null;
	}
}
