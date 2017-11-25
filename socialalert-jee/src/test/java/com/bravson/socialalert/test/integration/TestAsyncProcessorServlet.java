package com.bravson.socialalert.test.integration;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bravson.socialalert.infrastructure.async.AsyncEvent;
import com.bravson.socialalert.infrastructure.async.AsyncRepository;

@WebServlet("/unitTest/asyncProcessor")
public class TestAsyncProcessorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
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
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		repository.fireAsync(new TestEvent());
		try {
			observer.waitEvent();
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (InterruptedException e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
}
