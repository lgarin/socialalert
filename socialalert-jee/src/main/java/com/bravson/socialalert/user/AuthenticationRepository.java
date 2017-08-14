package com.bravson.socialalert.user;

import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.bravson.socialalert.infrastructure.log.Logged;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@ManagedBean
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Logged
public class AuthenticationRepository {
	
	private AuthenticationConfiguration config;
	
	private Client httpClient;

	@Inject
	public AuthenticationRepository(@NonNull AuthenticationConfiguration config, @NonNull Client httpClient) {
		this.config = config;
		this.httpClient = httpClient;
	}

	public Optional<String> requestAccessToken(@NonNull String userId, @NonNull String password) {
		Form form = new Form().param("username", userId).param("password", password).param("grant_type", "password").param("client_id", config.getLoginClientId()).param("client_secret", config.getClientSecret());
		Response response = httpClient.target(config.getLoginUrl()).request().post(Entity.form(form));
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return Optional.empty();
		}
		JsonObject payload = response.readEntity(JsonObject.class);
		return Optional.of("Bearer " + payload.getString("access_token"));
	}
	
	public boolean invalidateAccessToken(@NonNull String authorization) {
		Response response = httpClient.target(config.getLogoutUrl()).request().header("Authorization", authorization).get();
		return response.getStatus() == Status.OK.getStatusCode();
	}
	
	public Optional<UserInfo> findUserInfo(@NonNull String authorization) {
		Response response = httpClient.target(config.getUserInfoUrl()).request().header("Authorization", authorization).get();
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return Optional.empty();
		}
		return Optional.of(response.readEntity(UserInfo.class));
	}
}