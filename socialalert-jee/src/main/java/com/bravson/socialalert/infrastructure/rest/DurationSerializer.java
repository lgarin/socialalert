package com.bravson.socialalert.infrastructure.rest;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class DurationSerializer extends StdSerializer<Duration> {

	private static final long serialVersionUID = 1L;
	
	public DurationSerializer(){
        super(Duration.class);
    }

    @Override
    public void serialize(Duration value, JsonGenerator gen, SerializerProvider sp) throws IOException {
        gen.writeNumber(TimeUnit.SECONDS.toMillis(value.getSeconds()) + TimeUnit.NANOSECONDS.toMillis(value.getNano()));
    }

}
