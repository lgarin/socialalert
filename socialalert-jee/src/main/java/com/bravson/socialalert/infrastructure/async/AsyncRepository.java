package com.bravson.socialalert.infrastructure.async;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.enterprise.event.Event;
import javax.enterprise.event.NotificationOptions;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

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

	@Transactional(value = TxType.MANDATORY)
	public void fireAsync(AsyncEvent event) {
		txRegistry.registerInterposedSynchronization(new Synchronization() {
			
			@Override
			public void beforeCompletion() {
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
