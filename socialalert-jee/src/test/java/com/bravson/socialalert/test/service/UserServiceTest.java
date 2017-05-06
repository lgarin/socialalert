package com.bravson.socialalert.test.service;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.junit.Test;

import com.bravson.socialalert.user.UserInfo;

public class UserServiceTest extends BaseServiceTest {

	@Test
	@RunAsClient
	public void loginWithExistingUser() throws Exception {
		Form form = new Form("userId", "test@test.com").param("password", "123");
		Response response = createRequest("/user/login", MediaType.TEXT_PLAIN).post(Entity.form(form));
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertThat(response.readEntity(String.class).length()).isGreaterThan(64);
	}
	
	@Test
	@RunAsClient
	public void loginWithEmptyPassword() throws Exception {
		Form form = new Form("userId", "test@test.com").param("password", "");
		Response response = createRequest("/user/login", MediaType.TEXT_PLAIN).post(Entity.form(form));
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	@RunAsClient
	public void loginWithInvalidEmail() throws Exception {
		Form form = new Form("userId", "test").param("password", "abc");
		Response response = createRequest("/user/login", MediaType.TEXT_PLAIN).post(Entity.form(form));
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	@RunAsClient
	public void loginWithInvalidPassword() throws Exception {
		Form form = new Form("userId", "test@test.com").param("password", "abc");
		Response response = createRequest("/user/login", MediaType.TEXT_PLAIN).post(Entity.form(form));
		assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	@RunAsClient
	public void loginWithUnknownUser() throws Exception {
		Form form = new Form("userId", "xyz@test.com").param("password", "abc");
		Response response = createRequest("/user/login", MediaType.TEXT_PLAIN).post(Entity.form(form));
		assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	@RunAsClient
	public void logoutWithoutToken() throws Exception {
		Response response = createRequest("/user/logout", MediaType.TEXT_PLAIN).post(null);
		assertThat(response.getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());
	}
	
	@Test
	@RunAsClient
	public void logoutWithToken() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/user/logout", MediaType.TEXT_PLAIN, token).post(null);
		assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
	}
	
	@Test
	@RunAsClient
	public void logoutWithInvalidToken() throws Exception {
		Response response = createAuthRequest("/user/logout", MediaType.TEXT_PLAIN, "Bearer 12344334").post(null);
		assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	@RunAsClient
	public void getCurrentUserWithToken() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/user/current", MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
		UserInfo user = response.readEntity(UserInfo.class);
		assertThat(user).isNotNull();
		assertThat(user.getEmail()).isEqualTo("test@test.com");
	}
	
	@Test
	@RunAsClient
	public void getCurrentUserWithoutToken() throws Exception {
		Response response = createRequest("/user/current", MediaType.APPLICATION_JSON).get();
		assertThat(response.getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());
	}
}