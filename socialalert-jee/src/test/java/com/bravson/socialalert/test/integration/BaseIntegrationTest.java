package com.bravson.socialalert.test.integration;

import java.net.URL;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import com.bravson.socialalert.domain.user.LoginParameter;
import com.bravson.socialalert.domain.user.LoginResponse;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public abstract class BaseIntegrationTest extends Assertions {


	protected URL deploymentUrl;
	
	protected Builder createRequest(String path, String mediaType) {
		return ClientBuilder.newClient().target(deploymentUrl.toString() + "rest" + path).request(mediaType);
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
	public void cleanAllData() {
		Response response = ClientBuilder.newClient().target(deploymentUrl.toString() + "unitTest/deleteData").request().delete();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
	}
}
