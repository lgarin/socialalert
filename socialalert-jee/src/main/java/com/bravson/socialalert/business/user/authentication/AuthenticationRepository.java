package com.bravson.socialalert.business.user.authentication;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.bravson.socialalert.infrastructure.layer.Repository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Repository
@Transactional(TxType.SUPPORTS)
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class AuthenticationRepository {
	
	private static final Pattern JWT_TOKEN_PATTERN = Pattern.compile("^Bearer [A-Z0-9_\\-]+\\.([A-Z0-9_\\-]+)\\.[A-Z0-9_\\-]+$", Pattern.CASE_INSENSITIVE); 
	
	@Inject
	@NonNull
	AuthenticationConfiguration config;
	
	@Inject
	@NonNull
	Client httpClient;
	
	public Optional<String> extractUserId(@NonNull String accessToken) {
		Matcher matcher = JWT_TOKEN_PATTERN.matcher(accessToken);
		if (matcher.matches()) {
			byte[] body = Base64.getDecoder().decode(matcher.group(1));
			try (JsonReader reader = Json.createReader(new ByteArrayInputStream(body))) {
				return Optional.ofNullable(reader.readObject().getString("sub"));
			} catch (Exception e) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}

	public Optional<LoginToken> requestLoginToken(@NonNull String username, @NonNull String password) {
		Form form = new Form().param("username", username).param("password", password).param("grant_type", "password").param("client_id", config.getLoginClientId()).param("client_secret", config.getClientSecret());
		return postLoginTokenRequest(form);
	}

	private Optional<LoginToken> postLoginTokenRequest(Form form) {
		Response response = httpClient.target(config.getLoginUrl()).request().post(Entity.form(form));
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return Optional.empty();
		}
		JsonObject payload = response.readEntity(JsonObject.class);
		String accessToken = "Bearer " + payload.getString("access_token");
		String refreshToken = payload.getString("refresh_token");
		int expirationPeriod = payload.getInt("expires_in");
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
}
