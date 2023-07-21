package com.bravson.socialalert.infrastructure.async;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.NotificationOptions;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.transaction.Transactional;

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
	/*
	private static final String ASYNC_QUEUE_NAME = "async";
	
	@Inject
	ConnectionFactory connectionFactory;
	*/
	@Inject
	@Any
	Event<AsyncEvent> eventTrigger;
	
	@Inject
	Logger logger;
	
	@Inject
	TransactionSynchronizationRegistry txRegistry;

	@ConfigProperty(name = "async.threadCount", defaultValue = "1")
	int threadCount;
	
	ScheduledExecutorService scheduler;
	
	void onStart(@Observes StartupEvent ev) {
		scheduler = Executors.newScheduledThreadPool(threadCount);
		scheduler.submit(this);
	}

	void onStop(@Observes ShutdownEvent ev) {
		scheduler.shutdown();
	}
	
	@Override
	public void run() {
		/*
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
		*/
	}

	@Transactional
	public void fireAsync(AsyncEvent event) {
		// TODO use JMS
		txRegistry.registerInterposedSynchronization(new Synchronization() {
			
			@Override
			public void beforeCompletion() {
				// nothing to do
			}
			
			@Override
			public void afterCompletion(int status) {
				if (status == Status.STATUS_COMMITTED) {
					eventTrigger.fireAsync(event, NotificationOptions.ofExecutor(scheduler));
				}
				
			}
		});
		/*
		try (JMSContext context = connectionFactory.createContext(Session.SESSION_TRANSACTED)) {
            context.createProducer().send(context.createQueue(ASYNC_QUEUE_NAME), event);
            context.commit(); // TODO commit should be delayed
        }
        */
	}
}
