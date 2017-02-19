package com.bravson.socialalert;

import static org.junit.Assert.assertEquals;

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
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertThat(response.readEntity(String.class).length()).isGreaterThan(64);
	}
	
	@Test
	public void loginWithEmptyPassword() throws Exception {
		Form form = new Form("email", "test@test.com").param("password", "");
		Response response = createRequest("/user/login", MediaType.TEXT_PLAIN).post(Entity.form(form));
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	public void loginWithInvalidEmail() throws Exception {
		Form form = new Form("email", "test").param("password", "abc");
		Response response = createRequest("/user/login", MediaType.TEXT_PLAIN).post(Entity.form(form));
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	public void loginWithInvalidPassword() throws Exception {
		Form form = new Form("email", "test@test.com").param("password", "abc");
		Response response = createRequest("/user/login", MediaType.TEXT_PLAIN).post(Entity.form(form));
		assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	public void loginWithUnknownUser() throws Exception {
		Form form = new Form("email", "xyz@test.com").param("password", "abc");
		Response response = createRequest("/user/login", MediaType.TEXT_PLAIN).post(Entity.form(form));
		assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	public void logoutWithoutToken() throws Exception {
		Response response = createRequest("/user/logout", MediaType.TEXT_PLAIN).get();
		//assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	public void logoutWithToken() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/user/logout", MediaType.TEXT_PLAIN, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
	}
	
	@Test
	public void logoutWithInvalidToken() throws Exception {
		Response response = createAuthRequest("/user/logout", MediaType.TEXT_PLAIN, "12344334").get();
		assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	public void getCurrentUserWithToken() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/user/current", MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
		UserInfo user = response.readEntity(UserInfo.class);
		assertThat(user).isNotNull();
		assertThat(user.firstName).isEqualTo("Test");
		assertThat(user.lastName).isEqualTo("Hello");
		assertThat(user.email).isEqualTo("test@test.com");
		assertThat(user.groupNames).contains("Users");
	}
	
	@Test
	public void getCurrentUserWithoutToken() throws Exception {
		Response response = createRequest("/user/current", MediaType.APPLICATION_JSON).get();
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}
}