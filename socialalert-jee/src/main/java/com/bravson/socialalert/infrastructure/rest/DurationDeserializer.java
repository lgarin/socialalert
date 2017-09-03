package com.bravson.socialalert.infrastructure.rest;

import java.io.IOException;
import java.time.Duration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class DurationDeserializer extends StdDeserializer<Duration> {

	private static final long serialVersionUID = 1L;

	protected DurationDeserializer() {
        super(Duration.class);
    }

    @Override
    public Duration deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return Duration.ofMillis(jp.getLongValue());
    }

}
