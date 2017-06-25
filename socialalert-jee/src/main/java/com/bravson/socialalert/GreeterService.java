package com.bravson.socialalert;

import java.security.Principal;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.bravson.socialalert.infrastructure.log.Logged;
import com.bravson.socialalert.user.activity.SessionRepository;
import com.bravson.socialalert.user.activity.UserActivity;

@Path("/greeter")
@RolesAllowed("user")
@Logged
@UserActivity
public class GreeterService {

	@Inject
	Principal principal;
	
	@Inject
	SessionRepository sessionRepository;
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/hello")
	public String hello() {
		if (principal == null) {
			return "hello";
		}
		if (!sessionRepository.isUserActive(principal.getName())) {
			throw new RuntimeException("User not active");
		}
		return "hello " + principal;
	}
}
