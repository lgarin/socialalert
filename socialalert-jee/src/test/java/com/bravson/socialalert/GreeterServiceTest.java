package com.bravson.socialalert;

import static org.junit.Assert.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

public class GreeterServiceTest extends BaseServiceTest {

	@Test
	public void requestWithoutPrincial() throws Exception {

		Response response = createRequest("/greeter/hello", MediaType.TEXT_PLAIN).get();

		assertEquals(Status.FOUND.getStatusCode(), response.getStatus());
		assertTrue(response.getHeaderString("Location").startsWith("http://localhost:8080/auth/realms/SocialAlert-Dev/protocol/openid-connect/auth"));
	}

	@Test
	public void requestWithLogin() throws Exception {
		Form form = new Form("email", "test@test.com").param("password", "123");
		String token = createRequest("user/login", MediaType.TEXT_PLAIN).post(Entity.form(form)).readEntity(String.class);
		
		Response response = createRequest("/greeter/hello", MediaType.TEXT_PLAIN).header("Authorization", "bearer " + token).get();

		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertEquals("hello 4b09beae-9187-4566-b15a-b26f50dd840c", response.readEntity(String.class));
	}
}
