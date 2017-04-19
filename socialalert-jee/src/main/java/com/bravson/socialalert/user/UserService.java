package com.bravson.socialalert.user;

import java.security.Principal;

import javax.annotation.ManagedBean;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Path("/user")
@ManagedBean
@RolesAllowed("user")
public class UserService {
	
	@Inject
	Principal principal;
	
	@Inject
	AuthenticationRepository authenticationRepository;
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/login")
	@PermitAll
	public Response login(@Email @FormParam("userId") String userId, @NotEmpty @FormParam("password") String password) {
		String accessToken = authenticationRepository.requestAccessToken(userId, password).orElse(null);
		if (accessToken == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		return Response.status(Status.OK).entity(accessToken).build();
	}
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/logout")
	public Response logout(@NotEmpty @HeaderParam("Authorization") String authorization, @Context HttpServletRequest httpRequest) {
		Status status = authenticationRepository.invalidateAccessToken(authorization);
		if (status != Status.OK) {
			return Response.status(status).build();
		}
		httpRequest.getSession().invalidate();
		try {
			httpRequest.logout();
		} catch (ServletException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.status(Status.NO_CONTENT).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/current")
	public Response current(@NotEmpty @HeaderParam("Authorization") String authorization) {
		UserInfo userInfo = authenticationRepository.findUserInfo(principal.getName(), authorization).orElse(null);
		if (userInfo == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.status(Status.OK).entity(userInfo).build();
	}
}
