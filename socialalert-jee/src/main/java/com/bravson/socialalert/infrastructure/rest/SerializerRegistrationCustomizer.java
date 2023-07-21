package com.bravson.socialalert.infrastructure.rest;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;


@Singleton
public class SerializerRegistrationCustomizer implements ObjectMapperCustomizer {

    public void customize(ObjectMapper mapper) {
    	SimpleModule module = new SimpleModule();
    	module.addSerializer(Duration.class, new DurationSerializer());
    	module.addSerializer(Instant.class, new InstantSerializer());
    	module.addSerializer(LocalDate.class, new LocalDateSerializer());
    	
    	module.addDeserializer(Duration.class, new DurationDeserializer());
    	module.addDeserializer(Instant.class, new InstantDeserializer());
    	module.addDeserializer(LocalDate.class, new LocalDateDeserializer());
    	
    	mapper.registerModule(module);
    }
    
    @Override
    public int priority() {
    	return Integer.MIN_VALUE;
    }
}
