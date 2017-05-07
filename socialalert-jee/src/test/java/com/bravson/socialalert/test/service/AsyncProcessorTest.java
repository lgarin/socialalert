package com.bravson.socialalert.test.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.junit.Test;

import com.bravson.socialalert.file.video.AsyncVideoPreviewEvent;
import com.bravson.socialalert.infrastructure.async.AsyncRepository;

public class AsyncProcessorTest extends BaseServiceTest {
	
	@ManagedBean
	@ApplicationScoped
	public static class AsyncEventObserver {
		private final CountDownLatch asyncCountDown = new CountDownLatch(1);
		
		public void observerAsyncEvent(@Observes AsyncVideoPreviewEvent event) {
			asyncCountDown.countDown();
		}
		
		public void waitEvent() throws InterruptedException {
			boolean eventHandled = asyncCountDown.await(2, TimeUnit.SECONDS);
			if (!eventHandled) {
				throw new InterruptedException("Event not received");
			}
		}
	}
	
	@Inject
	private AsyncRepository repository;
	
	@Inject
	private AsyncEventObserver observer;
	
	@Test
	public void fireAsyncEvent() throws InterruptedException {
		repository.fireAsync(new AsyncVideoPreviewEvent());
		observer.waitEvent();
	}
}
