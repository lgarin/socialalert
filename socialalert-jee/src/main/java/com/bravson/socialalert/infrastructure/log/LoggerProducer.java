package com.bravson.socialalert.infrastructure.log;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class LoggerProducer {

	@Produces
	public Logger getLogger(InjectionPoint injectionPoint) {  
        return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass());
	}
}
