package com.bravson.socialalert.rest;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.UserLinkService;
import com.bravson.socialalert.business.user.UserService;
import com.bravson.socialalert.business.user.activity.UserActivity;
import com.bravson.socialalert.domain.user.LoginParameter;
import com.bravson.socialalert.domain.user.LoginResponse;
import com.bravson.socialalert.domain.user.UserInfo;

@Tag(name="user")
@Path("/user")
@RolesAllowed("user")
public class UserFacade {

	@Inject
	UserService userService;
	
	@Inject
	UserLinkService linkService;
	
	@Inject
	HttpServletRequest request;
	
	@Inject
	UserAccess userAccess;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/login")
	@PermitAll
	@Operation(summary="Login an existing user.")
	@APIResponse(responseCode = "200", description = "Login successfull.", content=@Content(schema=@Schema(implementation=LoginResponse.class)))
	@APIResponse(responseCode = "401", description = "Login failed.")
	public LoginResponse login(@Parameter(required=true) @Valid @NotNull LoginParameter param) {
		return userService.login(param, request.getRemoteAddr()).orElseThrow(() -> new WebApplicationException(Status.UNAUTHORIZED));
	}
	
	@POST
	@Path("/logout")
	@Operation(summary="Logout an existing user.")
	@APIResponse(responseCode = "204", description = "Logout successfull.")
	@APIResponse(responseCode = "400", description = "Logout failed.")
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
	@APIResponse(responseCode = "200", description = "Current user returned with success.", content=@Content(schema=@Schema(implementation=UserInfo.class)))
	@APIResponse(responseCode = "404", description = "Current user could not be found.")
	public UserInfo current(@Parameter(description="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) {
		return userService.findUserInfo(userAccess.getUserId()).orElseThrow(NotFoundException::new);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/info/{userId : .+}")
	@UserActivity
	@Operation(summary="Read information about the specified user.")
	@APIResponse(responseCode = "200", description = "Specified user returned with success.", content=@Content(schema=@Schema(implementation=UserInfo.class)))
	@APIResponse(responseCode = "404", description = "Specified user could not be found.")
	public UserInfo info(
			@Parameter(description="The user id to return", required=true) @NotEmpty @PathParam("userId") String userId,
			@Parameter(description="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) {
		return userService.findUserInfo(userId).orElseThrow(NotFoundException::new);
	}
	
	@POST
	@Path("/follow/{userId : .+}")
	@Operation(summary="Start following the specified user.")
	@APIResponse(responseCode = "200", description = "Link already exists.")
	@APIResponse(responseCode = "201", description = "Link has been created.")
	@APIResponse(responseCode = "404", description = "Specified user could not be found.")
	public Response follow(
			@Parameter(description="The user id to follow", required=true) @NotEmpty @PathParam("userId") String userId,
			@Parameter(description="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) {
		if (linkService.link(userAccess, userId)) {
			return Response.status(Status.CREATED).build();
		}
		return Response.status(Status.OK).build();
	}
	
	@POST
	@Path("/unfollow/{userId : .+}")
	@Operation(summary="Stop following the specified user.")
	@APIResponse(responseCode = "200", description = "Link has been deleted.")
	@APIResponse(responseCode = "410", description = "Link does not exist.")
	@APIResponse(responseCode = "404", description = "Specified user could not be found.")
	public Response unfollow(
			@Parameter(description="The user id to unfollow", required=true) @NotEmpty @PathParam("userId") String userId,
			@Parameter(description="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) {
		if (linkService.unlink(userAccess, userId)) {
			return Response.status(Status.OK).build();
		}
		return Response.status(Status.GONE).build();
	}
	
	@GET
	@Path("/followed")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary="List the followed users.")
	@APIResponse(responseCode = "200", description = "The has been deleted.", content=@Content(schema=@Schema(implementation=UserInfo.class, type = SchemaType.ARRAY)))
	public List<UserInfo> followedProfiles(@Parameter(description="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) {
		return linkService.getTargetProfiles(userAccess.getUserId());
	}
}
