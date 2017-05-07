package com.bravson.socialalert.test.service;

import java.time.Instant;
import java.util.Optional;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.junit.Test;

import com.bravson.socialalert.user.AuthenticationConfiguration;
import com.bravson.socialalert.user.AuthenticationRepository;
import com.bravson.socialalert.user.UserInfo;

public class AuthenticationRepositoryTest extends BaseServiceTest {

	private static AuthenticationConfiguration config = AuthenticationConfiguration
			.builder()
			.clientSecret("a1c5e3a3-a715-4f8e-b218-c826f9ebeac7")
			.loginClientId("socialalert-jee")
			.loginUrl("http://localhost:8080/auth/realms/SocialAlert-Dev/protocol/openid-connect/token")
			.logoutUrl("http://localhost:8080/auth/realms/SocialAlert-Dev/protocol/openid-connect/logout")
			.userInfoUrl("http://localhost:8080/auth/admin/realms/SocialAlert-Dev/users")
			.build();  
	
	private AuthenticationRepository repository = new AuthenticationRepository(config, ClientBuilder.newClient());
	
	@Test
	@RunAsClient
	public void requestAccessTokenWithValidCredentials() {
		Optional<String> result = repository.requestAccessToken("test@test.com", "123");
		assertThat(result).isPresent();
	}
	
	@Test
	@RunAsClient
	public void requestAccessTokenWithInvalidCredentials() {
		Optional<String> result = repository.requestAccessToken("test@test.com", "abc");
		assertThat(result).isEmpty();
	}
	
	@Test
	@RunAsClient
	public void invalidateInvalidAccessToken() {
		Status result = repository.invalidateAccessToken("Bearer abc");
		assertThat(result).isEqualTo(Status.OK); // TODO
	}
	
	@Test
	@RunAsClient
	public void invalidateValidAccessToken() {
		Optional<String> token = repository.requestAccessToken("test@test.com", "123");
		Status result = repository.invalidateAccessToken(token.get());
		assertThat(result).isEqualTo(Status.OK);
	}
	
	@Test
	@RunAsClient
	public void findExistingUserInfo() {
		Optional<String> token = repository.requestAccessToken("test@test.com", "123");
		Optional<UserInfo> result = repository.findUserInfo("33935dd6-d3b9-4215-9afa-984f5f28ccfc", token.get());
		assertThat(result).isPresent().hasValue(new UserInfo("33935dd6-d3b9-4215-9afa-984f5f28ccfc", "test@test.com", "test@test.com", Instant.ofEpochMilli(1492950170774L)));
	}
	
	@Test
	@RunAsClient
	public void findExistingUserInfoWithoutValidToken() {
		String token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ4NEgwcFNlU0NSa1Z3WklXQ3pVN25PVUtSOHNvMlM5dUJseGxfWUxkM3RJIn0.eyJqdGkiOiIwODNhODNhMi0wNzViLTQxN2MtOTMxZi05NTgwYjAxYTlhODQiLCJleHAiOjE0ODY2NjQwNDYsIm5iZiI6MCwiaWF0IjoxNDg2NjYzNzQ2LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXV0aC9yZWFsbXMvU29jaWFsQWxlcnQtRGV2IiwiYXVkIjoic29jaWFsYWxlcnQtamVlIiwic3ViIjoiNGIwOWJlYWUtOTE4Ny00NTY2LWIxNWEtYjI2ZjUwZGQ4NDBjIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoic29jaWFsYWxlcnQtamVlIiwiYXV0aF90aW1lIjowLCJzZXNzaW9uX3N0YXRlIjoiYTI2MzdhY2QtZDViNi00ODFkLWJmM2ItMTdiMTEwN2I1N2FjIiwiYWNyIjoiMSIsImNsaWVudF9zZXNzaW9uIjoiNTcxODY4MjYtZDNiNy00MzNmLWJmZjgtNWE0NWVjYmNlZTFmIiwiYWxsb3dlZC1vcmlnaW5zIjpbXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbInVtYV9hdXRob3JpemF0aW9uIiwidXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7InNvY2lhbGFsZXJ0LWplZSI6eyJyb2xlcyI6WyJ1c2VyIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50Iiwidmlldy1wcm9maWxlIl19fSwibmFtZSI6IlRlc3QgSGVsbG8iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0QHRlc3QuY29tIiwiZ2l2ZW5fbmFtZSI6IlRlc3QiLCJmYW1pbHlfbmFtZSI6IkhlbGxvIiwiZW1haWwiOiJ0ZXN0QHRlc3QuY29tIn0.GcuV77KQJiEltQso2a1anL6C1NcvL5eriLRGiZGsO5vjKRZyi2sPorAKCzBDcBJYYH4en2yoeJzLDEEHX5IiGEcJP8o-fZnZUxxAG6w8KeVn3lVY_pECqHtWvgnLPYJlUTdpO1qODFT0fioZqY-CnljF0y-HNeyXrX2qBGamS0ATW1MoWp80UGIaz0myLJ96yXFcjb4XrTj-dIqaAabsM5YZcQSaMA6qX63_3EPcX8o47rnYb5pxXFbwx5rKNtCeu9-F4m1va58Wzn8snKKiJQIT4fmEll4hX2tCHH3GIEL_5-5W7bPnJ4043KBKI6DvtaS1VeTcl0BZbu_YIVQlhg";
		Optional<UserInfo> result = repository.findUserInfo("33935dd6-d3b9-4215-9afa-984f5f28ccfc", token);
		assertThat(result).isEmpty();
	}
	
	@Test
	@RunAsClient
	public void findNonExistingUserInfo() {
		Optional<String> token = repository.requestAccessToken("test@test.com", "123");
		Optional<UserInfo> result = repository.findUserInfo("abc", token.get());
		assertThat(result).isEmpty();
	}
}
