package com.bravson.socialalert.infrastructure.util;

import java.time.Duration;

public interface DurationUtil {

	static Duration toDuration(Long millis) {
		return millis != null ? Duration.ofMillis(millis) : null;
	}
	
	static Long toMillis(Duration duration) {
		return duration != null ? duration.toMillis() : null;
	}
}
