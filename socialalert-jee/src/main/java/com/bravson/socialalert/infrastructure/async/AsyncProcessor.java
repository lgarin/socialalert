package com.bravson.socialalert.infrastructure.async;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.slf4j.Logger;

import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.eventbus.Message;

public class AsyncProcessor {

	@Inject 
	@Any Event<AsyncEvent> eventTrigger;
	
	@Inject
	Logger logger;
	
	@ConsumeEvent
	public void onMessage(Message<AsyncEvent> message) {
		try {
			logger.info("Processing message {}", message);
			AsyncEvent event = message.body();
			eventTrigger.fire(event);
		} catch (Exception e) {
			message.fail(0, e.getMessage());
			logger.error("Failed async processing for message " + message, e);
		}
	}

}
