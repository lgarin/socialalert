package com.bravson.socialalert;

import static org.junit.Assert.*;

import javax.json.JsonObject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.junit.Test;

public class UserServiceTest extends BaseServiceTest {

	@Test
	@RunAsClient
	public void loginWithExistingUser(@ArquillianResteasyResource WebTarget webTarget) throws Exception {
		Response response = webTarget.path("/user/login").request(MediaType.APPLICATION_JSON).header("email", "test@test.com").header("password", "123").get();
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		JsonObject payload = response.readEntity(JsonObject.class);
		assertTrue(payload.containsKey("access_token"));
	}
	
	@Test
	@RunAsClient
	public void loginWithInvalidPassword(@ArquillianResteasyResource WebTarget webTarget) throws Exception {
		Response response = webTarget.path("/user/login").request(MediaType.APPLICATION_JSON).header("email", "test@test.com").header("password", "abc").get();
		assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
	}
	
	@Test
	@RunAsClient
	public void loginWithUnknownUser(@ArquillianResteasyResource WebTarget webTarget) throws Exception {
		Response response = webTarget.path("/user/login").request(MediaType.APPLICATION_JSON).header("email", "xyz@test.com").header("password", "abc").get();
		assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
	}
}
