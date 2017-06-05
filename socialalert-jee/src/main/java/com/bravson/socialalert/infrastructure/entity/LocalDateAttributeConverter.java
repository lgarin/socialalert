package com.bravson.socialalert.infrastructure.entity;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, Date> {
	
    @Override
    public Date convertToDatabaseColumn(LocalDate localDate) {
    	return localDate == null ? null : Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant());
    }

    @Override
    public LocalDate convertToEntityAttribute(Date date) {
    	return date == null ? null : LocalDate.from(date.toInstant());
    }
}
