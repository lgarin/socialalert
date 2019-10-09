package com.bravson.socialalert.infrastructure.rest;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;


public class DurationSerializer implements JsonbSerializer<Duration> {

    @Override
    public void serialize(Duration value, JsonGenerator gen, SerializationContext  cxt) {
        gen.write(TimeUnit.SECONDS.toMillis(value.getSeconds()) + TimeUnit.NANOSECONDS.toMillis(value.getNano()));
    }

}
