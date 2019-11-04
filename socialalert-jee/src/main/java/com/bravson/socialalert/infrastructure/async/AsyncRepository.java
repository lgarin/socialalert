package com.bravson.socialalert.infrastructure.async;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.infrastructure.layer.Service;

import io.vertx.core.eventbus.EventBus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class AsyncRepository {

	/*
	@Inject
	EventBus bus;
	*/
	
	@Inject 
	@Any Event<AsyncEvent> eventTrigger;
	
	public void fireAsync(AsyncEvent event) {
		//bus.publish("async", event);
		eventTrigger.fireAsync(event);
    }
}
