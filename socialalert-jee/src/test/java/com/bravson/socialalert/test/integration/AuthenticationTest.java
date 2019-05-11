package com.bravson.socialalert.test.integration;

import java.time.Instant;
import java.util.Optional;

import javax.ws.rs.client.ClientBuilder;

import org.junit.Test;

import com.bravson.socialalert.business.user.AuthenticationConfiguration;
import com.bravson.socialalert.business.user.AuthenticationRepository;
import com.bravson.socialalert.domain.user.UserInfo;

public class AuthenticationTest extends BaseIntegrationTest {

	private static AuthenticationConfiguration config = AuthenticationConfiguration
			.builder()
			.clientSecret("335e1a19-1c27-4612-8997-32bffacab26b")
			.loginClientId("socialalert-jee")
			.loginUrl("http://localhost:8081/auth/realms/SocialAlert-Dev/protocol/openid-connect/token")
			.logoutUrl("http://localhost:8081/auth/realms/SocialAlert-Dev/protocol/openid-connect/logout")
			.userInfoUrl("http://localhost:8081/auth/realms/SocialAlert-Dev/protocol/openid-connect/userinfo")
			.build();  
	
	private AuthenticationRepository repository = new AuthenticationRepository(config, ClientBuilder.newClient());

	@Test
	public void decodeValidAccessToken() {
		String token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJCVkdSRzhLbEZ3Q2VXLXdHVlVCb1VlUVg2ZlNVVEp1RG5ZUDNIekJtV2ZVIn0.eyJqdGkiOiIzMGQzOWI1NC03ZDg2LTQzY2YtYTUxZS05MGY1MmRhOWY4NzciLCJleHAiOjE1MDQ1NDk0OTgsIm5iZiI6MCwiaWF0IjoxNTA0NTQ5MTk4LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXV0aC9yZWFsbXMvU29jaWFsQWxlcnQtRGV2IiwiYXVkIjoic29jaWFsYWxlcnQtamVlIiwic3ViIjoiMzM5MzVkZDYtZDNiOS00MjE1LTlhZmEtOTg0ZjVmMjhjY2ZjIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoic29jaWFsYWxlcnQtamVlIiwiYXV0aF90aW1lIjowLCJzZXNzaW9uX3N0YXRlIjoiOGQzOGEyZjYtMDRlZC00ZTUzLTk0ODctMWMxZDI4MGE3NWYyIiwiYWNyIjoiMSIsImNsaWVudF9zZXNzaW9uIjoiYjg4ZWRkMDctMzMyYy00Yzk4LThhY2MtZDI1MDI2MzM5MGM5IiwiYWxsb3dlZC1vcmlnaW5zIjpbIi9zb2NpYWxhbGVydC1qZWUiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbInVtYV9hdXRob3JpemF0aW9uIiwidXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7InJlYWxtLW1hbmFnZW1lbnQiOnsicm9sZXMiOlsidmlldy11c2VycyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwibmFtZSI6IiIsInByZWZlcnJlZF91c2VybmFtZSI6InRlc3RAdGVzdC5jb20iLCJlbWFpbCI6InRlc3RAdGVzdC5jb20ifQ.rG6XtBrPE8mTacgLoraY0qJVTOPvUed-cQp1FaNaJCWFpes_tVZFViC_zSCGAbS0q3qiW6D56M6Vi5iEIGJCzvJiBycBSfCy-bfAGpTNrNIjpgl8XtQUVkZArLu69qheFEbogjVI_c6bk7rsJrGZAxiBIO3TGGpzfA9mFD9uX27PWOS_Wiphw0hNj2oMhAscKw_BXP70k-LB6ZwzZp6sUJHevCAlRdrVVsys3ummVdVFqblCRY947qkNu2qqxEqeG-fnceQ_y5Y1EuP-lSh0G4k9SESR8UTmEj34s4bXXQ2ZX8FcfkZBbsLEcbPNvZqWLCR_shbtRU5y81G_4yWjpQ";
		Optional<String> result = repository.extractUserId(token);
		assertThat(result).hasValue("33935dd6-d3b9-4215-9afa-984f5f28ccfc");
	}
	
	@Test
	public void decodeInvalidAccessToken() {
		String token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJCVkdSRzhLbEZ3Q2VXLXdHVlVCb1VlUVg2ZlNVVEp1RG5ZUDNIekJtV2ZVIn0.fsdkiOiIzMGQzOWI1NC03ZDg2LTQzY2YtYTUxZS05MGY1MmRhOWY4NzciLCJleHAiOjE1MDQ1NDk0OTgsIm5iZiI6MCwiaWF0IjoxNTA0NTQ5MTk4LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXV0aC9yZWFsbXMvU29jaWFsQWxlcnQtRGV2IiwiYXVkIjoic29jaWFsYWxlcnQtamVlIiwic3ViIjoiMzM5MzVkZDYtZDNiOS00MjE1LTlhZmEtOTg0ZjVmMjhjY2ZjIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoic29jaWFsYWxlcnQtamVlIiwiYXV0aF90aW1lIjowLCJzZXNzaW9uX3N0YXRlIjoiOGQzOGEyZjYtMDRlZC00ZTUzLTk0ODctMWMxZDI4MGE3NWYyIiwiYWNyIjoiMSIsImNsaWVudF9zZXNzaW9uIjoiYjg4ZWRkMDctMzMyYy00Yzk4LThhY2MtZDI1MDI2MzM5MGM5IiwiYWxsb3dlZC1vcmlnaW5zIjpbIi9zb2NpYWxhbGVydC1qZWUiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbInVtYV9hdXRob3JpemF0aW9uIiwidXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7InJlYWxtLW1hbmFnZW1lbnQiOnsicm9sZXMiOlsidmlldy11c2VycyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwibmFtZSI6IiIsInByZWZlcnJlZF91c2VybmFtZSI6InRlc3RAdGVzdC5jb20iLCJlbWFpbCI6InRlc3RAdGVzdC5jb20ifQ.rG6XtBrPE8mTacgLoraY0qJVTOPvUed-cQp1FaNaJCWFpes_tVZFViC_zSCGAbS0q3qiW6D56M6Vi5iEIGJCzvJiBycBSfCy-bfAGpTNrNIjpgl8XtQUVkZArLu69qheFEbogjVI_c6bk7rsJrGZAxiBIO3TGGpzfA9mFD9uX27PWOS_Wiphw0hNj2oMhAscKw_BXP70k-LB6ZwzZp6sUJHevCAlRdrVVsys3ummVdVFqblCRY947qkNu2qqxEqeG-fnceQ_y5Y1EuP-lSh0G4k9SESR8UTmEj34s4bXXQ2ZX8FcfkZBbsLEcbPNvZqWLCR_shbtRU5y81G_4yWjpQ";
		Optional<String> result = repository.extractUserId(token);
		assertThat(result).isEmpty();
	}
	
	@Test
	public void requestAccessTokenWithValidCredentials() {
		Optional<String> result = repository.requestAccessToken("test@test.com", "123");
		assertThat(result).isPresent();
	}
	
	@Test
	public void requestAccessTokenWithInvalidCredentials() {
		Optional<String> result = repository.requestAccessToken("test@test.com", "abc");
		assertThat(result).isEmpty();
	}
	
	@Test
	public void invalidateInvalidAccessToken() {
		boolean result = repository.invalidateAccessToken("Bearer abc");
		assertThat(result).isTrue(); // TODO
	}
	
	@Test
	public void invalidateValidAccessToken() {
		Optional<String> token = repository.requestAccessToken("test@test.com", "123");
		boolean result = repository.invalidateAccessToken(token.get());
		assertThat(result).isTrue();
	}
	
	private UserInfo createExistingUserInfo() {
		return UserInfo.builder()
				.id("8b99179c-2a6b-4e41-92d3-3edfe3df885b")
				.username("test@test.com")
				.email("test@test.com")
				.createdTimestamp(Instant.ofEpochMilli(1557606426501L))
				.online(false)
				.build();
	}
	
	@Test
	public void findExistingUserInfo() {
		Optional<String> token = repository.requestAccessToken("test@test.com", "123");
		Optional<UserInfo> result = repository.findUserInfo(token.get());
		assertThat(result).isPresent().hasValue(createExistingUserInfo());
	}
	
	@Test
	public void findExistingUserInfoWithoutValidToken() {
		String token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ4NEgwcFNlU0NSa1Z3WklXQ3pVN25PVUtSOHNvMlM5dUJseGxfWUxkM3RJIn0.eyJqdGkiOiIwODNhODNhMi0wNzViLTQxN2MtOTMxZi05NTgwYjAxYTlhODQiLCJleHAiOjE0ODY2NjQwNDYsIm5iZiI6MCwiaWF0IjoxNDg2NjYzNzQ2LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXV0aC9yZWFsbXMvU29jaWFsQWxlcnQtRGV2IiwiYXVkIjoic29jaWFsYWxlcnQtamVlIiwic3ViIjoiNGIwOWJlYWUtOTE4Ny00NTY2LWIxNWEtYjI2ZjUwZGQ4NDBjIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoic29jaWFsYWxlcnQtamVlIiwiYXV0aF90aW1lIjowLCJzZXNzaW9uX3N0YXRlIjoiYTI2MzdhY2QtZDViNi00ODFkLWJmM2ItMTdiMTEwN2I1N2FjIiwiYWNyIjoiMSIsImNsaWVudF9zZXNzaW9uIjoiNTcxODY4MjYtZDNiNy00MzNmLWJmZjgtNWE0NWVjYmNlZTFmIiwiYWxsb3dlZC1vcmlnaW5zIjpbXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbInVtYV9hdXRob3JpemF0aW9uIiwidXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7InNvY2lhbGFsZXJ0LWplZSI6eyJyb2xlcyI6WyJ1c2VyIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50Iiwidmlldy1wcm9maWxlIl19fSwibmFtZSI6IlRlc3QgSGVsbG8iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0QHRlc3QuY29tIiwiZ2l2ZW5fbmFtZSI6IlRlc3QiLCJmYW1pbHlfbmFtZSI6IkhlbGxvIiwiZW1haWwiOiJ0ZXN0QHRlc3QuY29tIn0.GcuV77KQJiEltQso2a1anL6C1NcvL5eriLRGiZGsO5vjKRZyi2sPorAKCzBDcBJYYH4en2yoeJzLDEEHX5IiGEcJP8o-fZnZUxxAG6w8KeVn3lVY_pECqHtWvgnLPYJlUTdpO1qODFT0fioZqY-CnljF0y-HNeyXrX2qBGamS0ATW1MoWp80UGIaz0myLJ96yXFcjb4XrTj-dIqaAabsM5YZcQSaMA6qX63_3EPcX8o47rnYb5pxXFbwx5rKNtCeu9-F4m1va58Wzn8snKKiJQIT4fmEll4hX2tCHH3GIEL_5-5W7bPnJ4043KBKI6DvtaS1VeTcl0BZbu_YIVQlhg";
		Optional<UserInfo> result = repository.findUserInfo(token);
		assertThat(result).isEmpty();
	}
}
