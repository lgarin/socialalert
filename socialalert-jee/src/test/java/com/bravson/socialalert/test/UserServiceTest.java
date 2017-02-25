package com.bravson.socialalert.test;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import com.bravson.socialalert.user.Gender;
import com.bravson.socialalert.user.UserInfo;

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
		assertThat(response.getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());
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
		assertThat(user.getFirstName()).isEqualTo("Test");
		assertThat(user.getLastName()).isEqualTo("Hello");
		assertThat(user.getEmail()).isEqualTo("test@test.com");
		assertThat(user.getGroupNames()).contains("Users");
		assertThat(user.getCreatedTimestamp()).isEqualTo(Instant.ofEpochMilli(1486287913256L));
		assertThat(user.getAttributes().getBirthdate()).isEqualTo(LocalDate.of(1980, 2, 28));
		assertThat(user.getAttributes().getGender()).isEqualTo(Gender.MALE);
		assertThat(user.getAttributes().getLanguage()).isEqualTo("fr");
		assertThat(user.getAttributes().getCountry()).isEqualTo("CH");
		assertThat(user.getAttributes().getBiography()).isEqualTo("hello");
		assertThat(user.getAttributes().getImageUri()).isEqualTo(URI.create("test/test.png"));
	}
	
	@Test
	public void getCurrentUserWithoutToken() throws Exception {
		Response response = createRequest("/user/current", MediaType.APPLICATION_JSON).get();
		assertThat(response.getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());
	}
}