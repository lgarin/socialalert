package com.bravson.socialalert.business.user;

import java.security.Principal;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

@ManagedBean
@RequestScoped
public class UserAccessProducer {
	
	@Inject
	Principal principal;
	
	@Inject
	HttpServletRequest httpRequest;
	
	@Produces
	public UserAccess createUserAccess() {
		return UserAccess.of(principal.getName(), httpRequest.getRemoteAddr());
	}
}
