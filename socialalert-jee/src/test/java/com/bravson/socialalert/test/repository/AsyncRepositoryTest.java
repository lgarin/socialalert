package com.bravson.socialalert.test.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.infrastructure.async.AsyncEvent;
import com.bravson.socialalert.infrastructure.async.AsyncRepository;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class AsyncRepositoryTest {

	@Inject
	AsyncRepository repository;
	
	@Inject
	AsyncEventObserver observer;
	
	public static class TestEvent implements AsyncEvent {
		private static final long serialVersionUID = 1L;
	}
	
	@ApplicationScoped
	public static class AsyncEventObserver {
		private final CountDownLatch asyncCountDown = new CountDownLatch(1);
		
		public void observerAsyncEvent(@ObservesAsync TestEvent event) {
			asyncCountDown.countDown();
		}
		
		public boolean waitEvent() throws InterruptedException {
			return asyncCountDown.await(4, TimeUnit.SECONDS);
		}
	}
	
	@Test
	public void fireAsyncEvent() throws InterruptedException {
		repository.fireAsync(new TestEvent());
		boolean result = observer.waitEvent();
		assertTrue(result);
	}
}
