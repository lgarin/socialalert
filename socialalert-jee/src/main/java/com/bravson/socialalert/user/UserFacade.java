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

import com.bravson.socialalert.user.activity.UserActivity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(tags= {"user"})
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
	@ApiOperation(value="Login an existing user.")
	@ApiResponses(value= {
		@ApiResponse(code = 200, message = "Login successfull."),
		@ApiResponse(code = 401, message = "Login failed.") })
	public LoginResponse login(@ApiParam(required=true) @Valid @NotNull LoginParameter param) {
		return userService.login(param, request.getRemoteAddr()).orElseThrow(() -> new WebApplicationException(Status.UNAUTHORIZED));
	}
	
	@POST
	@Path("/logout")
	@ApiOperation(value="Logout an existing user.")
	@ApiResponses(value= {
			@ApiResponse(code = 204, message = "Logout successfull."),
			@ApiResponse(code = 400, message = "Logout failed.") })
	public Response logout(@ApiParam(value="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization, @Context HttpServletRequest httpRequest) throws ServletException {
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
	@ApiOperation(value="Read information about the currently logged in user.")
	@ApiResponses(value= {
			@ApiResponse(code = 200, message = "Current user returned with success."),
			@ApiResponse(code = 404, message = "Current user could not be found.") })
	public UserInfo current(@ApiParam(value="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) {
		return userService.findUserInfo(authorization).orElseThrow(NotFoundException::new);
	}
}
