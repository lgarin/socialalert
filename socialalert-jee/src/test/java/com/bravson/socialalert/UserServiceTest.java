package com.bravson.socialalert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

public class UserServiceTest extends BaseServiceTest {

	@Test
	public void loginWithExistingUser() throws Exception {
		Form form = new Form("email", "test@test.com").param("password", "123");
		Response response = createRequest("/user/login", MediaType.TEXT_PLAIN).post(Entity.form(form));
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		String token = response.readEntity(String.class);
		assertTrue(token.length() > 64);
	}
	
	@Test
	public void loginWithInvalidPassword() throws Exception {
		Form form = new Form("email", "test@test.com").param("password", "abc");
		Response response = createRequest("/user/login", MediaType.TEXT_PLAIN).post(Entity.form(form));
		assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void loginWithUnknownUser() throws Exception {
		Form form = new Form("email", "xyz@test.com").param("password", "abc");
		Response response = createRequest("/user/login", MediaType.TEXT_PLAIN).post(Entity.form(form));
		assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logoutWithoutToken() throws Exception {
		Response response = createRequest("/user/logout", MediaType.TEXT_PLAIN).get();
		assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void logoutWithToken() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/user/logout", MediaType.TEXT_PLAIN, token).get();
		assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
	}
}
