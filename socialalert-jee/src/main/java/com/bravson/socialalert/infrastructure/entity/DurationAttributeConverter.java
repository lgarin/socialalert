package com.bravson.socialalert.infrastructure.entity;

import java.time.Duration;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply=true)
public class DurationAttributeConverter implements AttributeConverter<Duration, Long> {
	
    @Override
    public Long convertToDatabaseColumn(Duration duration) {
    	return duration == null ? null : duration.toMillis();
    }

    @Override
    public Duration convertToEntityAttribute(Long millis) {
    	return millis == null ? null : Duration.ofMillis(millis);
    }
}
