package com.bravson.socialalert.infrastructure.entity;

import java.sql.Timestamp;
import java.time.Instant;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class InstantAttributeConverter implements AttributeConverter<Instant, Timestamp> {
	
    @Override
    public Timestamp convertToDatabaseColumn(Instant instant) {
    	return instant == null ? null : new Timestamp(instant.toEpochMilli());
    }

    @Override
    public Instant convertToEntityAttribute(Timestamp sqlTimestamp) {
    	return sqlTimestamp == null ? null : sqlTimestamp.toInstant();
    }
}
