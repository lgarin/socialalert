package com.bravson.socialalert.user;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hibernate.validator.constraints.NotEmpty;

import com.bravson.socialalert.infrastructure.log.Logged;
import com.bravson.socialalert.profile.ProfileRepository;
import com.bravson.socialalert.user.activity.UserActivity;

@Path("/user")
@RolesAllowed("user")
@Logged
public class UserService {
	
	@Inject
	AuthenticationRepository authenticationRepository;
	
	@Inject
	ProfileRepository profileRepository;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/login")
	@PermitAll
	public LoginResponse login(@Valid @NotNull LoginParameter param) {
		String accessToken = authenticationRepository.requestAccessToken(param.getUserId(), param.getPassword()).orElseThrow(() -> new WebApplicationException(Status.UNAUTHORIZED));
		if (!profileRepository.findByUserId(param.getUserId()).isPresent()) {
			UserInfo userInfo = authenticationRepository.findUserInfo(accessToken).orElseThrow(NotFoundException::new);
			profileRepository.createProfile(param.getUserId(), userInfo.getUsername(), userInfo.getCreatedTimestamp());
		}
		return new LoginResponse(accessToken);
	}
	
	@POST
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
	@UserActivity
	public Response current(@NotEmpty @HeaderParam("Authorization") String authorization) {
		UserInfo userInfo = authenticationRepository.findUserInfo(authorization).orElseThrow(NotFoundException::new);
		return Response.status(Status.OK).entity(userInfo).build();
	}
}
