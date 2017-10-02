package com.bravson.socialalert.infrastructure.entity;

import java.time.Instant;
import java.util.Date;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply=true)
public class InstantAttributeConverter implements AttributeConverter<Instant, Date> {
	
    @Override
    public Date convertToDatabaseColumn(Instant instant) {
    	return instant == null ? null : Date.from(instant);
    }

    @Override
    public Instant convertToEntityAttribute(Date timestamp) {
    	return timestamp == null ? null : timestamp.toInstant();
    }
}
