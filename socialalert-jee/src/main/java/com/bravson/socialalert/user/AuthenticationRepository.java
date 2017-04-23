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

@ManagedBean
public class AuthenticationRepository {
	
	private final AuthenticationConfiguration config;
	
	private final Client httpClient;

	@Inject
	public AuthenticationRepository(AuthenticationConfiguration config, Client httpClient) {
		this.config = config;
		this.httpClient = httpClient;
	}

	public Optional<String> requestAccessToken(String userId, String password) {
		Form form = new Form().param("username", userId).param("password", password).param("grant_type", "password").param("client_id", config.getLoginClientId()).param("client_secret", config.getClientSecret());
		Response response = httpClient.target(config.getLoginUrl()).request().post(Entity.form(form));
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return Optional.empty();
		}
		JsonObject payload = response.readEntity(JsonObject.class);
		return Optional.of("Bearer " + payload.getString("access_token"));
	}
	
	public Status invalidateAccessToken(String authorization) {
		Response response = httpClient.target(config.getLogoutUrl()).request().header("Authorization", authorization).get();
		return Status.fromStatusCode(response.getStatus());
	}
	
	public Optional<UserInfo> findUserInfo(String id, String authorization) {
		Response response = httpClient.target(config.getUserInfoUrl()).path(id).request().header("Authorization", authorization).get();
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return Optional.empty();
		}
		return Optional.of(response.readEntity(UserInfo.class));
	}
}
