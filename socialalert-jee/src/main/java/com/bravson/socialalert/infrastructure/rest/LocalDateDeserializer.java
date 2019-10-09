package com.bravson.socialalert.infrastructure.rest;

import java.lang.reflect.Type;
import java.time.LocalDate;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

public class LocalDateDeserializer implements JsonbDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt, Type type) {
        return LocalDate.parse(jp.getString());
    }

}
