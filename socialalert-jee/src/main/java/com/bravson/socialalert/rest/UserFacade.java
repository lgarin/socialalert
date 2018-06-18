package com.bravson.socialalert.rest;

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

import com.bravson.socialalert.business.user.UserService;
import com.bravson.socialalert.business.user.activity.UserActivity;
import com.bravson.socialalert.domain.user.LoginParameter;
import com.bravson.socialalert.domain.user.LoginResponse;
import com.bravson.socialalert.domain.user.UserInfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="user")
@Path("/user")
@RolesAllowed("user")
public class UserFacade {

	@Inject
	UserService userService;
	
	@Inject
	HttpServletRequest request;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/login")
	@PermitAll
	@Operation(summary="Login an existing user.")
	@ApiResponse(responseCode = "200", description = "Login successfull.")
	@ApiResponse(responseCode = "401", description = "Login failed.")
	public LoginResponse login(@Parameter(required=true) @Valid @NotNull LoginParameter param) {
		return userService.login(param, request.getRemoteAddr()).orElseThrow(() -> new WebApplicationException(Status.UNAUTHORIZED));
	}
	
	@POST
	@Path("/logout")
	@Operation(summary="Logout an existing user.")
	@ApiResponse(responseCode = "204", description = "Logout successfull.")
	@ApiResponse(responseCode = "400", description = "Logout failed.")
	public Response logout(@Parameter(description="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization, @Context HttpServletRequest httpRequest) throws ServletException {
		if (!userService.logout(authorization)) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		httpRequest.getSession().invalidate();
		httpRequest.logout();
		return Response.status(Status.NO_CONTENT).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/current")
	@UserActivity
	@Operation(summary="Read information about the currently logged in user.")
	@ApiResponses(value= {
			@ApiResponse(responseCode = "200", description = "Current user returned with success."),
			@ApiResponse(responseCode = "404", description = "Current user could not be found.") })
	public UserInfo current(@Parameter(description="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) {
		return userService.findUserInfo(authorization).orElseThrow(NotFoundException::new);
	}
}
