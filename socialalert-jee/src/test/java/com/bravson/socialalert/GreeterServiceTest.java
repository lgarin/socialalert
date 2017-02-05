package com.bravson.socialalert;

import static org.junit.Assert.assertEquals;

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

		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertEquals("hello anonymous", response.readEntity(String.class));
	}

	@Test
	public void requestWithLogin() throws Exception {
		Form form = new Form("email", "test@test.com").param("password", "123");
		String token = createRequest("user/login", MediaType.TEXT_PLAIN).post(Entity.form(form)).readEntity(String.class);
		
		Response response = createRequest("/greeter/hello", MediaType.TEXT_PLAIN).header("Authorization ", "Bearer " + token).get();

		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertEquals("hello test", response.readEntity(String.class));
	}
}
