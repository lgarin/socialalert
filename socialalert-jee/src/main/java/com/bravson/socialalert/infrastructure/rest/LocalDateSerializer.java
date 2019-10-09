package com.bravson.socialalert.infrastructure.rest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

public class LocalDateSerializer implements JsonbSerializer<LocalDate> {

	@Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializationContext  cxt) {
        gen.write(value.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
}
