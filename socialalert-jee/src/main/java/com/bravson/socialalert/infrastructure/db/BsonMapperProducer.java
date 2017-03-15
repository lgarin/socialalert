package com.bravson.socialalert.infrastructure.db;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.mongodb.morphia.Morphia;

@ManagedBean
@ApplicationScoped
public class BsonMapperProducer {

	@Produces
	@ApplicationScoped
	public Morphia createMapper() {
		Morphia morphia = new Morphia();
		morphia.getMapper().getConverters().addConverter(new OptionalConverter(morphia.getMapper().getConverters()));
		morphia.mapPackage("com.bravson.socialalert");
		return morphia;
	}
}