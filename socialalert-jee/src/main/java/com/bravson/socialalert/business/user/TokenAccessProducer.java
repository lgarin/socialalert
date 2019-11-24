package com.bravson.socialalert.business.user;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.eclipse.microprofile.jwt.JsonWebToken;

@RequestScoped
public class TokenAccessProducer {
	
	@Produces
	@TokenAccess
	public UserAccess createUserAccess(@Context JsonWebToken token, @Context HttpServletRequest httpRequest) {
		Optional<String> userId = Optional.ofNullable(token).map(JsonWebToken::getSubject);
		Optional<String> ipAddress = Optional.ofNullable(httpRequest).map(HttpServletRequest::getRemoteAddr);
		return UserAccess.of(userId.orElse("annoym"), ipAddress.orElse("0.0.0.0"));
	}
}
