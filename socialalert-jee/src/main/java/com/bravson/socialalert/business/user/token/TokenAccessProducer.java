package com.bravson.socialalert.business.user.token;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

@RequestScoped
public class TokenAccessProducer {
	
	@Produces
	public UserAccess createUserAccess(@Context JsonWebToken token, @Context HttpServletRequest httpRequest) {
		Optional<String> userId = Optional.ofNullable(token).map(JsonWebToken::getSubject);
		Optional<String> ipAddress = Optional.ofNullable(httpRequest).map(HttpServletRequest::getRemoteAddr);
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
