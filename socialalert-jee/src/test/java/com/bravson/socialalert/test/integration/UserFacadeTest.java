package com.bravson.socialalert.test.integration;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.junit.Test;

import com.bravson.socialalert.domain.user.LoginParameter;
import com.bravson.socialalert.domain.user.LoginResponse;
import com.bravson.socialalert.domain.user.UserInfo;

public class UserFacadeTest extends BaseIntegrationTest {

	@Test
	@RunAsClient
	public void loginWithExistingUser() throws Exception {
		LoginParameter param = new LoginParameter("test@test.com", "123");
		Response response = createRequest("/user/login", MediaType.APPLICATION_JSON).post(Entity.json(param));
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
		LoginResponse result = response.readEntity(LoginResponse.class); 
		assertThat(result).isNotNull();
		assertThat(result.getAccessToken()).startsWith("Bearer ").matches(s -> s.length() > 64);
	}
	
	@Test
	@RunAsClient
	public void loginWithEmptyPassword() throws Exception {
		LoginParameter param = new LoginParameter("test@test.com", "");
		Response response = createRequest("/user/login", MediaType.APPLICATION_JSON).post(Entity.json(param));
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	@RunAsClient
	public void loginWithEmptyUserId() throws Exception {
		LoginParameter param = new LoginParameter("", "abc");
		Response response = createRequest("/user/login", MediaType.APPLICATION_JSON).post(Entity.json(param));
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	@RunAsClient
	public void loginWithInvalidPassword() throws Exception {
		LoginParameter param = new LoginParameter("test@test.com", "abc");
		Response response = createRequest("/user/login", MediaType.APPLICATION_JSON).post(Entity.json(param));
		assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	@RunAsClient
	public void loginWithUnknownUser() throws Exception {
		LoginParameter param = new LoginParameter("xyz@test.com", "abc");
		Response response = createRequest("/user/login", MediaType.APPLICATION_JSON).post(Entity.json(param));
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