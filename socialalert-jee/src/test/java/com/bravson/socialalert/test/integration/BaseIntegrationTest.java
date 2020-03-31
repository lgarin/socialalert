package com.bravson.socialalert.test.integration;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.business.user.activity.OnlineUserCache;
import com.bravson.socialalert.domain.user.LoginParameter;
import com.bravson.socialalert.domain.user.LoginResponse;
import com.bravson.socialalert.infrastructure.async.AsyncEvent;
import com.bravson.socialalert.infrastructure.entity.PersistenceManager;
import com.bravson.socialalert.infrastructure.rest.MediaTypeConstants;

import io.quarkus.test.common.http.TestHTTPResource;

public abstract class BaseIntegrationTest extends Assertions {

	@TestHTTPResource
	protected URL deploymentUrl;
	
	@Inject
	Client httpClient;
	
	@Inject
	FileStore fileStore;
	
	@Inject
	PersistenceManager persistenceManager;
	
	@Inject
	AsyncEventObserver asyncEventObserver;
	
	@Inject
	OnlineUserCache onlineUserCache;
	
	protected Builder createRequest(String path, String mediaType) {
		return httpClient.target(deploymentUrl.toString() + "rest" + path).request(mediaType);
	}
	
	protected Builder createAuthRequest(String path, String mediaType, String token) {
		return createRequest(path, mediaType).header("Authorization", token);
	}
	
	protected String requestLoginToken(String userId, String password) {
		LoginParameter param = new LoginParameter(userId,password);
		LoginResponse response = createRequest("/user/login", MediaTypeConstants.JSON).post(Entity.json(param)).readEntity(LoginResponse.class);
		return response.getAccessToken();
	}
	
	protected String getLocationPath(Response response) {
		return response.getLocation().toString().replaceFirst("^(http://.*/rest)", "");
	}
	
	@AfterEach
	public void closeHttpClient() {
		httpClient.close();
	}
	
	@BeforeEach
	public void cleanAllData() throws IOException {
		persistenceManager.deleteAll();
		fileStore.deleteAllFiles();
		onlineUserCache.clearAll();
	}
	
	@ApplicationScoped
	public static class AsyncEventObserver {
		private final Map<Class<? extends AsyncEvent>, Semaphore> map = new ConcurrentHashMap<>();
		
		public void observerAsyncEvent(@ObservesAsync AsyncEvent event) {
			getSemaphore(event.getClass()).release();
		}

		private Semaphore getSemaphore(Class<? extends AsyncEvent> eventClass) {
			return map.computeIfAbsent(eventClass, k -> new Semaphore(0));
		}
		
		public void waitEvent(Class<? extends AsyncEvent> eventClass) throws InterruptedException {
			boolean eventHandled = getSemaphore(eventClass).tryAcquire(30, TimeUnit.SECONDS);
			if (!eventHandled) {
				throw new InterruptedException("Event not received");
			}
		}
	}
	
	protected void awaitAsyncEvent(Class<? extends AsyncEvent> eventClass) throws InterruptedException {
		asyncEventObserver.waitEvent(eventClass);
	}
}
