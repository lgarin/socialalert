package com.bravson.socialalert.infrastructure.rest;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;


public class DurationSerializer extends JsonSerializer<Duration> {

	@Override
	public void serialize(Duration value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeNumber(TimeUnit.SECONDS.toMillis(value.getSeconds()) + TimeUnit.NANOSECONDS.toMillis(value.getNano()));
	}

}
