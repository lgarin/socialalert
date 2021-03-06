package com.bravson.socialalert.infrastructure.rest;

import java.io.IOException;
import java.time.Duration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;


public class DurationDeserializer extends JsonDeserializer<Duration> {

	@Override
	public Duration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		return Duration.ofMillis(p.getLongValue());
	}

}
