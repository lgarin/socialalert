package com.bravson.socialalert;

import java.security.Principal;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/greeter")
@ManagedBean
public class GreeterService {

	@Inject
	private Principal principal;
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/hello")
	public String hello() {
		if (principal == null) {
			return "hello";
		}
		return "hello " + principal;
	}
}
