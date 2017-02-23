package com.bravson.socialalert.infrastructure.rest;

import java.io.IOException;
import java.time.Instant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;


public class InstantDeserializer extends StdDeserializer<Instant> {

	private static final long serialVersionUID = 1L;

	protected InstantDeserializer() {
        super(Instant.class);
    }

    @Override
    public Instant deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return Instant.ofEpochMilli(jp.getLongValue());
    }

}
