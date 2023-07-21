package com.bravson.socialalert.business.user.authentication;

import java.time.Instant;
import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import com.bravson.socialalert.domain.user.CreateUserParameter;
import com.bravson.socialalert.infrastructure.layer.Repository;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Repository
@Transactional(TxType.SUPPORTS)
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class AuthenticationRepository {
	
	private static final String AUTHORIZATION_HEADER = "Authorization";

	@Inject
	@NonNull
	AuthenticationConfiguration config;
	
	@Inject
	@NonNull
	Client httpClient;

	public Optional<LoginToken> requestLoginToken(@NonNull String username, @NonNull String password) {
		Form form = new Form().param("username", username).param("password", password).param("grant_type", "password").param("client_id", config.loginClientId()).param("client_secret", config.clientSecret());
		return postLoginTokenRequest(form);
	}

	private Optional<LoginToken> postLoginTokenRequest(Form form) {
		Response response = httpClient.target(config.loginUrl()).request().post(Entity.form(form));
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return Optional.empty();
		}
		JsonNode payload = response.readEntity(JsonNode.class);
		String accessToken = "Bearer " + payload.get("access_token").asText();
		String refreshToken = payload.get("refresh_token").asText();
		int expirationPeriod = payload.get("expires_in").asInt();
		Instant expiration = Instant.now().plusSeconds(expirationPeriod - 1L);
		return Optional.of(LoginToken.of(accessToken, refreshToken, expiration));
	}
	
	public Optional<LoginToken> refreshLoginToken(@NonNull String refreshToken) {
		Form form = new Form().param("refresh_token", refreshToken).param("grant_type", "refresh_token").param("client_id", config.loginClientId()).param("client_secret", config.clientSecret());
		return postLoginTokenRequest(form);
	}
	
	public boolean invalidateAccessToken(@NonNull String authorization) {
		Response response = httpClient.target(config.logoutUrl()).request().header(AUTHORIZATION_HEADER, authorization).get();
		return response.getStatus() == Status.OK.getStatusCode();
	}
	
	public Optional<AuthenticationInfo> findAuthenticationInfo(@NonNull String authorization) {
		Response response = httpClient.target(config.userInfoUrl()).request().header(AUTHORIZATION_HEADER, authorization).get();
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return Optional.empty();
		}
		return Optional.of(response.readEntity(AuthenticationInfo.class));
	}
	
	public boolean isAvailable() {
		Response response = httpClient.target(config.configUrl()).request().get();
		return response.getStatus() == Status.OK.getStatusCode();
	}
	
	public boolean createUser(@NonNull CreateUserParameter param) {
		String authorization = getAdminAuthorization();
		
		UserRepresentation user = UserRepresentation.builder()
				.username(param.getUsername())
				.email(param.getEmail())
				.firstName(param.getFirstName())
				.lastName(param.getLastName())
				.enabled(true)
				.credential(new CredentialRepresentation(false, "password", param.getPassword()))
				.build();
		Response response = httpClient.target(config.userCreateUrl()).request()
				.header(AUTHORIZATION_HEADER, authorization).post(Entity.json(user));
		if (response.getStatus() == Status.CREATED.getStatusCode()) {
			return true;
		} else if (response.getStatus() == Status.CONFLICT.getStatusCode()) {
			return false;
		}
		throw new ClientErrorException(response.getStatus());
	}
	
	public void updateUser(@NonNull String userId, String firstname, String lastname) {
		String authorization = getAdminAuthorization();
		
		UserRepresentation user = UserRepresentation.builder()
				.firstName(firstname == null ? "" : firstname)
				.lastName(lastname == null ? "" : lastname)
				.build();
		Response response = httpClient.target(config.userUpdateUrl()).resolveTemplate("id", userId)
				.request().header(AUTHORIZATION_HEADER, authorization).put(Entity.json(user));

		if (response.getStatus() != Status.NO_CONTENT.getStatusCode()) {
			throw new ClientErrorException(response.getStatus());
		}
	}
	
	public void deleteUser(@NonNull String userId) {
		String authorization = getAdminAuthorization();
		
		Response response = httpClient.target(config.userUpdateUrl()).resolveTemplate("id", userId)
				.request().header(AUTHORIZATION_HEADER, authorization).delete();

		if (response.getStatus() != Status.NO_CONTENT.getStatusCode()) {
			throw new ClientErrorException(response.getStatus());
		}
	}

	private String getAdminAuthorization() {
		Form form = new Form().param("username", config.adminUsername()).param("password", config.adminPassword()).param("grant_type", "password").param("client_id", config.adminClientId());
		Response response = httpClient.target(config.adminLoginUrl()).request().post(Entity.form(form));
		if (response.getStatus() != Status.OK.getStatusCode()) {
			throw new ClientErrorException(response.getStatus());
		}
		JsonNode payload = response.readEntity(JsonNode.class);
		return "Bearer " + payload.get("access_token").asText();
	}
	
	public void changePassword(@NonNull String userId, @NonNull String newPassword) {
		String authorization = getAdminAuthorization();
		
		CredentialRepresentation credential = new CredentialRepresentation(false, "password", newPassword);
		Response response = httpClient.target(config.passwordResetUrl()).resolveTemplate("id", userId)
				.request().header(AUTHORIZATION_HEADER, authorization).put(Entity.json(credential));

		if (response.getStatus() != Status.NO_CONTENT.getStatusCode()) {
			throw new ClientErrorException(response.getStatus());
		}
	}
}
