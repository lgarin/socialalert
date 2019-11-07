package com.bravson.socialalert.infrastructure.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Session;
import javax.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

import com.bravson.socialalert.infrastructure.layer.Service;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AsyncRepository implements Runnable {

	private static final String ASYNC_QUEUE_NAME = "async";

	@Inject
	ConnectionFactory connectionFactory;
	
	@Inject
	@Any
	Event<AsyncEvent> eventTrigger;
	
	@Inject
	Logger logger;
	
	@ConfigProperty(name = "async.threadCount", defaultValue = "1")
	int threadCount;
	
	ExecutorService scheduler;

	void onStart(@Observes StartupEvent ev) {
		scheduler = Executors.newFixedThreadPool(threadCount);
		scheduler.submit(this);
	}

	void onStop(@Observes ShutdownEvent ev) {
		scheduler.shutdown();
	}

	@Override
	public void run() {
		try (JMSContext context = connectionFactory.createContext(Session.CLIENT_ACKNOWLEDGE)) {
			JMSConsumer consumer = context.createConsumer(context.createQueue(ASYNC_QUEUE_NAME));
			while (true) {
				AsyncEvent event = consumer.receiveBody(AsyncEvent.class);
				if (event == null) {
					return;
				}
				try {
					eventTrigger.fire(event);
					context.acknowledge();
				} catch (Exception e) {
					logger.error("Failed event processing", e);
				}
			}
		}
	}

	public void fireAsync(AsyncEvent event) {
		try (JMSContext context = connectionFactory.createContext(Session.SESSION_TRANSACTED)) {
            context.createProducer().send(context.createQueue(ASYNC_QUEUE_NAME), event);
            context.commit(); // TODO commit should be delayed
        }
	}
}
