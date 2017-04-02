package com.bravson.socialalert.infrastructure.util;

import java.util.Date;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public interface DateUtil {

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
