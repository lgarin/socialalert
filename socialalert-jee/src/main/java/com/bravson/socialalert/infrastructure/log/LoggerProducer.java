package com.bravson.socialalert.infrastructure.log;

import javax.annotation.ManagedBean;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean
public class LoggerProducer {

	@Produces
	public Logger getLogger(InjectionPoint injectionPoint) {  
        return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass());
	}
}
