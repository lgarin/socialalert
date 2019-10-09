package com.bravson.socialalert.infrastructure.rest;

import java.lang.reflect.Type;
import java.time.Duration;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;


public class DurationDeserializer implements JsonbDeserializer<Duration> {

    @Override
    public Duration deserialize(JsonParser jp, DeserializationContext ctxt, Type type) {
        return Duration.ofMillis(jp.getLong());
    }

}
