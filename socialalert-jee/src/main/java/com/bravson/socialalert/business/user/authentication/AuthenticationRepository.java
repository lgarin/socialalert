package com.bravson.socialalert.business.user.authentication;

import java.time.Instant;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
	
	@Inject
	@NonNull
	AuthenticationConfiguration config;
	
	@Inject
	@NonNull
	Client httpClient;

	public Optional<LoginToken> requestLoginToken(@NonNull String username, @NonNull String password) {
		Form form = new Form().param("username", username).param("password", password).param("grant_type", "password").param("client_id", config.getLoginClientId()).param("client_secret", config.getClientSecret());
		return postLoginTokenRequest(form);
	}

	private Optional<LoginToken> postLoginTokenRequest(Form form) {
		Response response = httpClient.target(config.getLoginUrl()).request().post(Entity.form(form));
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return Optional.empty();
		}
		JsonNode payload = response.readEntity(JsonNode.class);
		String accessToken = "Bearer " + payload.get("access_token").asText();
		String refreshToken = payload.get("refresh_token").asText();
		int expirationPeriod = payload.get("expires_in").asInt();
		Instant expiration = Instant.now().plusSeconds(expirationPeriod - 1);
		return Optional.of(LoginToken.of(accessToken, refreshToken, expiration));
	}
	
	public Optional<LoginToken> refreshLoginToken(@NonNull String refreshToken) {
		Form form = new Form().param("refresh_token", refreshToken).param("grant_type", "refresh_token").param("client_id", config.getLoginClientId()).param("client_secret", config.getClientSecret());
		return postLoginTokenRequest(form);
	}
	
	public boolean invalidateAccessToken(@NonNull String authorization) {
		Response response = httpClient.target(config.getLogoutUrl()).request().header("Authorization", authorization).get();
		return response.getStatus() == Status.OK.getStatusCode();
	}
	
	public Optional<AuthenticationInfo> findAuthenticationInfo(@NonNull String authorization) {
		Response response = httpClient.target(config.getUserInfoUrl()).request().header("Authorization", authorization).get();
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return Optional.empty();
		}
		return Optional.of(response.readEntity(AuthenticationInfo.class));
	}
	
	public boolean isAvailable() {
		Response response = httpClient.target(config.getConfigUrl()).request().get();
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return false;
		}
		return true;
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
		Response response = httpClient.target(config.getUserCreateUrl()).request()
				.header("Authorization", authorization).post(Entity.json(user));
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
		Response response = httpClient.target(config.getUserUpdateUrl()).resolveTemplate("id", userId)
				.request().header("Authorization", authorization).put(Entity.json(user));

		if (response.getStatus() != Status.NO_CONTENT.getStatusCode()) {
			throw new ClientErrorException(response.getStatus());
		}
	}
	
	public void deleteUser(@NonNull String userId) {
		String authorization = getAdminAuthorization();
		
		Response response = httpClient.target(config.getUserUpdateUrl()).resolveTemplate("id", userId)
				.request().header("Authorization", authorization).delete();

		if (response.getStatus() != Status.NO_CONTENT.getStatusCode()) {
			throw new ClientErrorException(response.getStatus());
		}
	}

	private String getAdminAuthorization() {
		Form form = new Form().param("username", config.getAdminUsername()).param("password", config.getAdminPassword()).param("grant_type", "password").param("client_id", config.getAdminClientId());
		Response response = httpClient.target(config.getAdminLoginUrl()).request().post(Entity.form(form));
		if (response.getStatus() != Status.OK.getStatusCode()) {
			throw new ClientErrorException(response.getStatus());
		}
		JsonNode payload = response.readEntity(JsonNode.class);
		return "Bearer " + payload.get("access_token").asText();
	}
	
	public void changePassword(@NonNull String userId, @NonNull String newPassword) {
		String authorization = getAdminAuthorization();
		
		CredentialRepresentation credential = new CredentialRepresentation(false, "password", newPassword);
		Response response = httpClient.target(config.getPasswordResetUrl()).resolveTemplate("id", userId)
				.request().header("Authorization", authorization).put(Entity.json(credential));

		if (response.getStatus() != Status.NO_CONTENT.getStatusCode()) {
			throw new ClientErrorException(response.getStatus());
		}
	}
}
