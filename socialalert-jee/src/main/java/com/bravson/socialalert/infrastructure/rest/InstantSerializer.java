package com.bravson.socialalert.infrastructure.rest;

import java.io.IOException;
import java.time.Instant;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class InstantSerializer extends StdSerializer<Instant> {

	private static final long serialVersionUID = 1L;
	
	public InstantSerializer(){
        super(Instant.class);
    }

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider sp) throws IOException {
        gen.writeNumber(value.toEpochMilli());
    }

}
