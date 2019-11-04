package com.bravson.socialalert.business.user;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.microprofile.jwt.JsonWebToken;

@RequestScoped
public class TokenAccessProducer {
	
	@Inject
	JsonWebToken token;
	
	@Inject
	HttpServletRequest httpRequest;
	
	@Produces
	@TokenAccess
	public UserAccess createUserAccess() {
		String userId = token.getSubject();
		String ipAddress = httpRequest.getRemoteAddr();
		return UserAccess.of(userId != null ? userId : "anonym", ipAddress != null ? ipAddress : "0.0.0.0");
	}
}
