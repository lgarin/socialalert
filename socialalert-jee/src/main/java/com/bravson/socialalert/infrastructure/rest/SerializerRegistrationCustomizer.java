package com.bravson.socialalert.infrastructure.rest;

import javax.inject.Singleton;
import javax.json.bind.JsonbConfig;

import io.quarkus.jsonb.JsonbConfigCustomizer;

@Singleton
public class SerializerRegistrationCustomizer implements JsonbConfigCustomizer {
	
	@Override
	public void customize(JsonbConfig config) {
		config.withSerializers(
				new DurationSerializer(),
				new InstantSerializer(),
				new LocalDateSerializer());
		
		config.withDeserializers(
				new DurationDeserializer(),
				new InstantDeserializer(),
				new LocalDateDeserializer());
	}

}
