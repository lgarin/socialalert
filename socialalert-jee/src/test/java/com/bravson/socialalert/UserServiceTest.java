package com.bravson.socialalert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.junit.Test;

public class UserServiceTest extends BaseServiceTest {

	@Test
	@RunAsClient
	public void loginWithExistingUser() throws Exception {
		Form form = new Form("email", "test@test.com").param("password", "123");
		Response response = createRequest("/user/login", MediaType.TEXT_PLAIN).post(Entity.form(form));
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		String token = response.readEntity(String.class);
		assertTrue(token.length() > 64);
	}
	
	@Test
	@RunAsClient
	public void loginWithInvalidPassword() throws Exception {
		Form form = new Form("email", "test@test.com").param("password", "abc");
		Response response = createRequest("/user/login", MediaType.TEXT_PLAIN).post(Entity.form(form));
		assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
	}
	
	@Test
	@RunAsClient
	public void loginWithUnknownUser(@ArquillianResteasyResource WebTarget webTarget) throws Exception {
		Form form = new Form("email", "xyz@test.com").param("password", "abc");
		Response response = createRequest("/user/login", MediaType.TEXT_PLAIN).post(Entity.form(form));
		assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
	}
}
