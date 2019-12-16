package com.bravson.socialalert.test.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.user.authentication.AuthenticationInfo;
import com.bravson.socialalert.business.user.authentication.AuthenticationRepository;
import com.bravson.socialalert.business.user.authentication.LoginToken;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class AuthenticationRepositoryTest {

	@Inject
	private AuthenticationRepository repository;
	
	@Test
	public void requestLoginTokenWithValidCredentials() {
		Optional<LoginToken> result = repository.requestLoginToken("test@test.com", "123");
		assertThat(result).isPresent();
	}
	
	@Test
	public void requestLoginTokenWithInvalidCredentials() {
		Optional<LoginToken> result = repository.requestLoginToken("test@test.com", "abc");
		assertThat(result).isEmpty();
	}
	
	@Test
	public void refreshAccessTokenWithValidCredentials() {
		LoginToken loginToken = repository.requestLoginToken("test@test.com", "123").get();
		Optional<LoginToken> result = repository.refreshLoginToken(loginToken.getRefreshToken());
		assertThat(result).isPresent();
		assertThat(result.get().getRefreshToken()).isNotEqualTo(loginToken.getRefreshToken());
		assertThat(result.get().getAccessToken()).isNotEqualTo(loginToken.getAccessToken());
	}
	
	@Test
	public void invalidateInvalidAccessToken() {
		boolean result = repository.invalidateAccessToken("Bearer abc");
		assertThat(result).isTrue(); // TODO
	}
	
	@Test
	public void invalidateValidAccessToken() {
		Optional<LoginToken> token = repository.requestLoginToken("test@test.com", "123");
		boolean result = repository.invalidateAccessToken(token.get().getAccessToken());
		assertThat(result).isTrue();
	}
	
	private AuthenticationInfo createExistingAuthenticationInfo() {
		return AuthenticationInfo.builder()
				.id("8b99179c-2a6b-4e41-92d3-3edfe3df885b")
				.username("test@test.com")
				.email("test@test.com")
				.createdTimestamp(Instant.ofEpochMilli(1557606426501L))
				.emailVerified(true)
				.build();
	}
	
	@Test
	public void findExistingAuthenticationInfo() {
		Optional<LoginToken> token = repository.requestLoginToken("test@test.com", "123");
		Optional<AuthenticationInfo> result = repository.findAuthenticationInfo(token.get().getAccessToken());
		assertThat(result).isPresent().hasValue(createExistingAuthenticationInfo());
	}
	
	@Test
	public void findExistingAuthenticationInfoWithoutValidToken() {
		String token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ4NEgwcFNlU0NSa1Z3WklXQ3pVN25PVUtSOHNvMlM5dUJseGxfWUxkM3RJIn0.eyJqdGkiOiIwODNhODNhMi0wNzViLTQxN2MtOTMxZi05NTgwYjAxYTlhODQiLCJleHAiOjE0ODY2NjQwNDYsIm5iZiI6MCwiaWF0IjoxNDg2NjYzNzQ2LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXV0aC9yZWFsbXMvU29jaWFsQWxlcnQtRGV2IiwiYXVkIjoic29jaWFsYWxlcnQtamVlIiwic3ViIjoiNGIwOWJlYWUtOTE4Ny00NTY2LWIxNWEtYjI2ZjUwZGQ4NDBjIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoic29jaWFsYWxlcnQtamVlIiwiYXV0aF90aW1lIjowLCJzZXNzaW9uX3N0YXRlIjoiYTI2MzdhY2QtZDViNi00ODFkLWJmM2ItMTdiMTEwN2I1N2FjIiwiYWNyIjoiMSIsImNsaWVudF9zZXNzaW9uIjoiNTcxODY4MjYtZDNiNy00MzNmLWJmZjgtNWE0NWVjYmNlZTFmIiwiYWxsb3dlZC1vcmlnaW5zIjpbXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbInVtYV9hdXRob3JpemF0aW9uIiwidXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7InNvY2lhbGFsZXJ0LWplZSI6eyJyb2xlcyI6WyJ1c2VyIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50Iiwidmlldy1wcm9maWxlIl19fSwibmFtZSI6IlRlc3QgSGVsbG8iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0QHRlc3QuY29tIiwiZ2l2ZW5fbmFtZSI6IlRlc3QiLCJmYW1pbHlfbmFtZSI6IkhlbGxvIiwiZW1haWwiOiJ0ZXN0QHRlc3QuY29tIn0.GcuV77KQJiEltQso2a1anL6C1NcvL5eriLRGiZGsO5vjKRZyi2sPorAKCzBDcBJYYH4en2yoeJzLDEEHX5IiGEcJP8o-fZnZUxxAG6w8KeVn3lVY_pECqHtWvgnLPYJlUTdpO1qODFT0fioZqY-CnljF0y-HNeyXrX2qBGamS0ATW1MoWp80UGIaz0myLJ96yXFcjb4XrTj-dIqaAabsM5YZcQSaMA6qX63_3EPcX8o47rnYb5pxXFbwx5rKNtCeu9-F4m1va58Wzn8snKKiJQIT4fmEll4hX2tCHH3GIEL_5-5W7bPnJ4043KBKI6DvtaS1VeTcl0BZbu_YIVQlhg";
		Optional<AuthenticationInfo> result = repository.findAuthenticationInfo(token);
		assertThat(result).isEmpty();
	}
}
