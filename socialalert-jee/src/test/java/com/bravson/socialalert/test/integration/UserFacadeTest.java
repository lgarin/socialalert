package com.bravson.socialalert.test.integration;

import java.time.Instant;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.user.authentication.AuthenticationInfo;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.domain.user.LoginParameter;
import com.bravson.socialalert.domain.user.LoginResponse;
import com.bravson.socialalert.domain.user.UserInfo;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class UserFacadeTest extends BaseIntegrationTest {

	@Inject
	private UserProfileRepository profileRepository;
	
	@Test
	public void loginWithExistingUser() throws Exception {
		createProfile("test@test.com");
		
		LoginParameter param = new LoginParameter("test@test.com", "123");
		Response response = createRequest("/user/login", MediaType.APPLICATION_JSON).post(Entity.json(param));
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
		LoginResponse result = response.readEntity(LoginResponse.class); 
		assertThat(result).isNotNull();
		assertThat(result.getUsername()).isEqualTo("test@test.com");
		assertThat(result.getAccessToken()).startsWith("Bearer ").matches(s -> s.length() > 64);
	}

	private UserProfileEntity createProfile(String username) {
		AuthenticationInfo authInfo = AuthenticationInfo.builder()
				.id("8b99179c-2a6b-4e41-92d3-3edfe3df885b")
				.email(username)
				.username(username)
				.createdTimestamp(Instant.EPOCH)
				.build();
		return profileRepository.createProfile(authInfo, "1.2.3.4");
	}
	
	@Test
	public void loginWithEmptyPassword() throws Exception {
		LoginParameter param = new LoginParameter("test@test.com", "");
		Response response = createRequest("/user/login", MediaType.APPLICATION_JSON).post(Entity.json(param));
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	public void loginWithEmptyUserId() throws Exception {
		LoginParameter param = new LoginParameter("", "abc");
		Response response = createRequest("/user/login", MediaType.APPLICATION_JSON).post(Entity.json(param));
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	public void loginWithInvalidPassword() throws Exception {
		LoginParameter param = new LoginParameter("test@test.com", "abc");
		Response response = createRequest("/user/login", MediaType.APPLICATION_JSON).post(Entity.json(param));
		assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	public void loginWithUnknownUser() throws Exception {
		LoginParameter param = new LoginParameter("xyz@test.com", "abc");
		Response response = createRequest("/user/login", MediaType.APPLICATION_JSON).post(Entity.json(param));
		assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	public void logoutWithoutToken() throws Exception {
		Response response = createRequest("/user/logout", MediaType.TEXT_PLAIN).post(null);
		assertThat(response.getStatus()).isEqualTo(Status.FOUND.getStatusCode());
	}
	
	@Test
	public void logoutWithToken() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/user/logout", MediaType.TEXT_PLAIN, token).post(null);
		assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
	}
	
	@Test
	public void logoutWithInvalidToken() throws Exception {
		Response response = createAuthRequest("/user/logout", MediaType.TEXT_PLAIN, "Bearer 12344334").post(null);
		assertThat(response.getStatus()).isEqualTo(Status.INTERNAL_SERVER_ERROR.getStatusCode());
	}
	
	@Test
	public void getCurrentUserWithToken() throws Exception {
		createProfile("test@test.com");
		
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/user/current", MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
		UserInfo user = response.readEntity(UserInfo.class);
		assertThat(user).isNotNull();
		assertThat(user.getEmail()).isEqualTo("test@test.com");
	}
	
	@Test
	public void getCurrentUserWithoutToken() throws Exception {
		Response response = createRequest("/user/current", MediaType.APPLICATION_JSON).get();
		assertThat(response.getStatus()).isEqualTo(Status.FOUND.getStatusCode());
	}
}