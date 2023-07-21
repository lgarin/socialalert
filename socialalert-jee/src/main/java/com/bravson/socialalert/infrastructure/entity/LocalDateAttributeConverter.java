package com.bravson.socialalert.infrastructure.entity;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply=true)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, Date> {
	
    @Override
    public Date convertToDatabaseColumn(LocalDate localDate) {
    	return localDate == null ? null : Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant());
    }

    @Override
    public LocalDate convertToEntityAttribute(Date date) {
    	return date == null ? null : LocalDate.ofInstant(date.toInstant(), ZoneOffset.UTC);
    }
}
