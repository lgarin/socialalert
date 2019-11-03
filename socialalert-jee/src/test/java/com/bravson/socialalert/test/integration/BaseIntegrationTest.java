package com.bravson.socialalert.test.integration;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.domain.user.LoginParameter;
import com.bravson.socialalert.domain.user.LoginResponse;
import com.bravson.socialalert.infrastructure.entity.PersistenceManager;

import io.quarkus.test.common.http.TestHTTPResource;

public abstract class BaseIntegrationTest extends Assertions {

	@TestHTTPResource
	protected URL deploymentUrl;
	
	@Inject
	private Client httpClient;
	
	@Inject
	private FileStore fileStore;
	
	@Inject
	private PersistenceManager persistenceManager;
	
	protected Builder createRequest(String path, String mediaType) {
		return httpClient.target(deploymentUrl.toString() + "rest" + path).request(mediaType);
	}
	
	protected Builder createAuthRequest(String path, String mediaType, String token) {
		return createRequest(path, mediaType).header("Authorization", token);
	}
	
	protected String requestLoginToken(String userId, String password) {
		LoginParameter param = new LoginParameter(userId,password);
		LoginResponse response = createRequest("/user/login", MediaType.APPLICATION_JSON).post(Entity.json(param)).readEntity(LoginResponse.class);
		return response.getAccessToken();
	}
	
	protected String getLocationPath(Response response) {
		return response.getLocation().toString().replaceFirst("^(http://.*/rest)", "");
	}
	
	@BeforeEach
	public void cleanAllData() throws IOException {
		persistenceManager.deleteAll();
		fileStore.deleteAllFiles();
	}
}
