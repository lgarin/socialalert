package com.bravson.socialalert.test.integration;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.junit.Test;

import com.bravson.socialalert.infrastructure.async.AsyncEvent;
import com.bravson.socialalert.infrastructure.async.AsyncRepository;

public class AsyncProcessorTest extends BaseIntegrationTest {
	
	public static class TestEvent implements AsyncEvent {
		private static final long serialVersionUID = 1L;
	}
	
	@ManagedBean
	@ApplicationScoped
	public static class AsyncEventObserver {
		private final CountDownLatch asyncCountDown = new CountDownLatch(1);
		
		public void observerAsyncEvent(@Observes TestEvent event) {
			asyncCountDown.countDown();
		}
		
		public void waitEvent() throws InterruptedException {
			boolean eventHandled = asyncCountDown.await(4, TimeUnit.SECONDS);
			if (!eventHandled) {
				throw new InterruptedException("Event not received");
			}
		}
	}
	
	@Inject
	AsyncRepository repository;
	
	@Inject
	AsyncEventObserver observer;
	
	@Test
	public void fireAsyncEvent() throws InterruptedException {
		repository.fireAsync(new TestEvent());
		observer.waitEvent();
	}
}
