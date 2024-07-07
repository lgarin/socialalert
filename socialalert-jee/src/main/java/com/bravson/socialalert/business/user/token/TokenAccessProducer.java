package com.bravson.socialalert.business.user.token;

import java.util.Optional;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.net.HostAndPort;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.ws.rs.core.Context;

@RequestScoped
public class TokenAccessProducer {
	
	@Produces
	public UserAccess createUserAccess(@Context JsonWebToken token, @Context HttpServerRequest httpRequest) {
		Optional<String> userId = Optional.ofNullable(token).map(JsonWebToken::getSubject);
		Optional<String> ipAddress = Optional.ofNullable(httpRequest).map(HttpServerRequest::authority).map(HostAndPort::host);
		Optional<String> username = Optional.ofNullable(token).map(t -> t.getClaim(Claims.preferred_username.name()));
		Optional<String> email = Optional.ofNullable(token).map(t -> t.getClaim(Claims.email.name()));
		return UserAccessToken.builder()
				.userId(userId.orElse("annoym"))
				.ipAddress(ipAddress.orElse("0.0.0.0"))
				.username(username.orElse("annoym"))
				.email(email.orElse("annoym"))
				.build();
	}
}
