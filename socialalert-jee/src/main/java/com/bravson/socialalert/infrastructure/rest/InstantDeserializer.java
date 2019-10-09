package com.bravson.socialalert.infrastructure.rest;

import java.lang.reflect.Type;
import java.time.Instant;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

public class InstantDeserializer implements JsonbDeserializer<Instant> {

    @Override
    public Instant deserialize(JsonParser jp, DeserializationContext ctxt, Type type) {
        return Instant.ofEpochMilli(jp.getLong());
    }

}
