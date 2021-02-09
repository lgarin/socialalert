package com.bravson.socialalert.infrastructure.rest;

import java.io.IOException;
import java.time.Instant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;


public class InstantDeserializer extends JsonDeserializer<Instant> {

	@Override
	public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		return Instant.ofEpochMilli(p.getLongValue());
	}
}
