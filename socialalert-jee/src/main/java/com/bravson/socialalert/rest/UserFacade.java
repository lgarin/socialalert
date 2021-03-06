package com.bravson.socialalert.rest;

import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.bravson.socialalert.business.media.comment.MediaCommentService;
import com.bravson.socialalert.business.user.TokenAccess;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.UserLinkService;
import com.bravson.socialalert.business.user.UserProfileService;
import com.bravson.socialalert.business.user.UserService;
import com.bravson.socialalert.business.user.activity.UserActivity;
import com.bravson.socialalert.domain.media.comment.UserCommentDetail;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.domain.user.ChangePasswordParameter;
import com.bravson.socialalert.domain.user.CreateUserParameter;
import com.bravson.socialalert.domain.user.LoginResponse;
import com.bravson.socialalert.domain.user.LoginTokenResponse;
import com.bravson.socialalert.domain.user.UserCredential;
import com.bravson.socialalert.domain.user.UserDetail;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.domain.user.privacy.UserPrivacy;
import com.bravson.socialalert.domain.user.profile.UpdateProfileParameter;
import com.bravson.socialalert.infrastructure.rest.MediaTypeConstants;

@Tag(name="user")
@Path("/user")
@RolesAllowed("user")
public class UserFacade {

	@Inject
	UserService userService;
	
	@Inject
	UserLinkService linkService;
	
	@Inject
	UserProfileService profileService;
	
	@Inject
	MediaCommentService commentService;
	
	@Inject
	@TokenAccess
	Instance<UserAccess> userAccess;
	
	@POST
	@Consumes(MediaTypeConstants.JSON)
	@Produces(MediaTypeConstants.JSON)
	@Path("/create")
	@PermitAll
	@Operation(summary="Create a new user.")
	@APIResponse(responseCode = "409", description = "User already exists.")
	@APIResponse(responseCode = "201", description = "User created with success.")
	public Response create(@Parameter(required = true) @Valid @NotNull CreateUserParameter param) {
		if (userService.createUser(param)) {
			return Response.status(Status.CREATED).build();
		}
		return Response.status(Status.CONFLICT).build();
	}
	
	@POST
	@Consumes(MediaTypeConstants.JSON)
	@Produces(MediaTypeConstants.JSON)
	@Path("/login")
	@PermitAll
	@Operation(summary="Login an existing user.")
	@APIResponse(responseCode = "200", description = "Login successfull.", content=@Content(schema=@Schema(implementation=LoginResponse.class)))
	@APIResponse(responseCode = "401", description = "Login failed.")
	public Response login(@Parameter(required=true) @Valid @NotNull UserCredential param) throws ServletException {
		LoginResponse loginResponse = userService.login(param, userAccess.get().getIpAddress()).orElse(null);
		if (loginResponse == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		return Response.status(Status.OK).entity(loginResponse).build();
	}
	
	@POST
	@Consumes(MediaTypeConstants.TEXT_PLAIN)
	@Produces(MediaTypeConstants.JSON)
	@Path("/renewLogin")
	@PermitAll
	@Operation(summary="Renew an existing login using the previous refresh token.")
	@APIResponse(responseCode = "200", description = "Login successfull.", content=@Content(schema=@Schema(implementation=LoginResponse.class)))
	@APIResponse(responseCode = "401", description = "Login failed.")
	public Response renewLogin(@Parameter(required=true) @Valid @NotNull String refreshToken) throws ServletException {
		LoginTokenResponse tokenResponse = userService.renewLogin(refreshToken, userAccess.get().getIpAddress()).orElse(null);
		if (tokenResponse == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		return Response.status(Status.OK).entity(tokenResponse).build();
	}
	
	@POST
	@Path("/logout")
	@Operation(summary="Logout an existing user.")
	@SecurityRequirement(name = "JWT")
	@APIResponse(responseCode = "204", description = "Logout successfull.")
	@APIResponse(responseCode = "400", description = "Logout failed.")
	public Response logout(@Context SecurityContext securityContext) throws ServletException {
		if (securityContext.getUserPrincipal() instanceof JsonWebToken) {
			JsonWebToken token = (JsonWebToken) securityContext.getUserPrincipal();
			if (!userService.logout("Bearer" + token.getRawToken())) {
				return Response.status(Status.BAD_REQUEST).build();
			}
			return Response.status(Status.NO_CONTENT).build();
		}
		return Response.status(Status.FORBIDDEN).build();
	}
	
	@POST
	@Consumes(MediaTypeConstants.JSON)
	@Path("/changePassword")
	@PermitAll
	@Operation(summary="Change the current user password.")
	@SecurityRequirement(name = "JWT")
	@APIResponse(responseCode = "204", description = "Password changed")
	@APIResponse(responseCode = "401", description = "Invalid credentials")
	public Response changePassword(@Parameter(required=true) @Valid @NotNull ChangePasswordParameter param) throws ServletException {
		LoginResponse loginResponse = userService.login(param, userAccess.get().getIpAddress()).orElse(null);
		if (loginResponse == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		userService.changePassword(loginResponse.getId(), param.getNewPassword());
		return Response.status(Status.NO_CONTENT).build();
	}
	
	@GET
	@Produces(MediaTypeConstants.JSON)
	@Path("/current")
	@UserActivity
	@Operation(summary="Read information about the currently logged in user.")
	@SecurityRequirement(name = "JWT")
	@APIResponse(responseCode = "200", description = "Current user returned with success.", content=@Content(schema=@Schema(implementation=UserInfo.class)))
	@APIResponse(responseCode = "404", description = "Current user could not be found.")
	public UserDetail current() {
		return userService.findOwnUserInfo(userAccess.get().getUserId()).orElseThrow(NotFoundException::new);
	}
	
	@GET
	@Produces(MediaTypeConstants.JSON)
	@Path("/info/{userId : .+}")
	@UserActivity
	@Operation(summary="Read information about the specified user.")
	@SecurityRequirement(name = "JWT")
	@APIResponse(responseCode = "200", description = "Specified user returned with success.", content=@Content(schema=@Schema(implementation=UserInfo.class)))
	@APIResponse(responseCode = "404", description = "Specified user could not be found.")
	public UserInfo info(
			@Parameter(description="The user id to return", required=true) @NotEmpty @PathParam("userId") String userId) {
		UserInfo result = userService.findUserInfo(userId).orElseThrow(NotFoundException::new);
		result.setFollowedSince(linkService.findLinkCreationTimetamp(userAccess.get(), userId).orElse(null));
		return result;
	}
	
	@GET
	@Produces(MediaTypeConstants.JSON)
	@Path("/comments/{userId : .+}")
	@UserActivity
	@Operation(summary="List the comments posted by the specified user.")
	@SecurityRequirement(name = "JWT")
	@APIResponse(responseCode = "200", description = "The matching comments are available in the response.")
	@APIResponse(responseCode = "404", description = "Specified user could not be found.")
	public QueryResult<UserCommentDetail> listComments(@Parameter(description="The user id to return", required=true) @NotEmpty @PathParam("userId") String userId,
			@Parameter(description="Sets the timestamp in milliseconds since the epoch when the paging started.", required=false) @Min(0) @QueryParam("pagingTimestamp") Long pagingTimestamp,
			@Parameter(description="Sets the page number to return.", required=false) @DefaultValue("0") @Min(0) @QueryParam("pageNumber") int pageNumber,
			@Parameter(description="Sets the size of the page to return.", required=false) @DefaultValue("20") @Min(1) @Max(100) @QueryParam("pageSize")  int pageSize) {
		
		return commentService.listUserComments(userId, PagingParameter.of(pagingTimestamp, pageNumber, pageSize));
	}
	
	@POST
	@Produces(MediaTypeConstants.JSON)
	@Path("/follow/{userId : .+}")
	@UserActivity
	@Operation(summary="Start following the specified user.")
	@SecurityRequirement(name = "JWT")
	@APIResponse(responseCode = "200", description = "Link already exists.")
	@APIResponse(responseCode = "201", description = "Link has been created.")
	@APIResponse(responseCode = "404", description = "Specified user could not be found.")
	public Response follow(
			@Parameter(description="The user id to follow", required=true) @NotEmpty @PathParam("userId") String userId) {
		UserInfo targetUserInfo = linkService.link(userAccess.get(), userId).orElse(null);
		if (targetUserInfo != null) {
			return Response.status(Status.CREATED).entity(targetUserInfo).build();
		}
		return Response.status(Status.OK).build();
	}
	
	@POST
	@Produces(MediaTypeConstants.JSON)
	@Path("/unfollow/{userId : .+}")
	@UserActivity
	@Operation(summary="Stop following the specified user.")
	@SecurityRequirement(name = "JWT")
	@APIResponse(responseCode = "200", description = "Link has been deleted.")
	@APIResponse(responseCode = "410", description = "Link does not exist.")
	@APIResponse(responseCode = "404", description = "Specified user could not be found.")
	public Response unfollow(
			@Parameter(description="The user id to unfollow", required=true) @NotEmpty @PathParam("userId") String userId) {
		UserInfo targetUserInfo = linkService.unlink(userAccess.get(), userId).orElse(null);
		if (targetUserInfo != null) {
			return Response.status(Status.OK).entity(targetUserInfo).build();
		}
		return Response.status(Status.GONE).build();
	}
	
	@GET
	@Path("/followed")
	@Produces(MediaTypeConstants.JSON)
	@UserActivity
	@Operation(summary="List the followed users.")
	@SecurityRequirement(name = "JWT")
	@APIResponse(responseCode = "200", description = "List of users returned with success.", content=@Content(schema=@Schema(implementation=UserInfo.class, type = SchemaType.ARRAY)))
	public List<UserInfo> getFollowedProfiles() {
		return linkService.getTargetProfiles(userAccess.get().getUserId());
	}
	
	@GET
	@Path("/followers")
	@Produces(MediaTypeConstants.JSON)
	@UserActivity
	@Operation(summary="Page for the followers of the current user.")
	@SecurityRequirement(name = "JWT")
	public QueryResult<UserInfo> listFollowers(@Parameter(description="Sets the timestamp in milliseconds since the epoch when the paging started.", required=false) @Min(0) @QueryParam("pagingTimestamp") Long pagingTimestamp,
			@Parameter(description="Sets the page number to return.", required=false) @DefaultValue("0") @Min(0) @QueryParam("pageNumber") int pageNumber,
			@Parameter(description="Sets the size of the page to return.", required=false) @DefaultValue("20") @Min(1) @Max(100) @QueryParam("pageSize")  int pageSize) {
		return linkService.listSourceProfiles(userAccess.get().getUserId(), PagingParameter.of(pagingTimestamp, pageNumber, pageSize));
	}
	
	@POST
	@Consumes(MediaTypeConstants.JSON)
	@Produces(MediaTypeConstants.JSON)
	@Path("/profile")
	@UserActivity
	@Operation(summary="Update own profile.")
	@SecurityRequirement(name = "JWT")
	@APIResponse(responseCode = "200", description = "Profile has been updated.", content=@Content(schema=@Schema(implementation=UserInfo.class)))
	public UserInfo updateProfile(@Parameter(required = true) @Valid @NotNull UpdateProfileParameter param) {
		return profileService.updateProfile(param, userAccess.get());
	}
	
	@POST
	@Consumes(MediaTypeConstants.JSON)
	@Produces(MediaTypeConstants.JSON)
	@Path("/privacy")
	@UserActivity
	@Operation(summary="Update own privacy settings.")
	@SecurityRequirement(name = "JWT")
	@APIResponse(responseCode = "200", description = "Profile has been updated.", content=@Content(schema=@Schema(implementation=UserInfo.class)))
	public UserDetail updatePrivacy(@Parameter(required = true) @Valid @NotNull UserPrivacy param) {
		return profileService.updatePrivacy(param, userAccess.get());
	}
	
	@GET
	@Path("/countries")
	@PermitAll
	@Produces(MediaTypeConstants.JSON)
	@Operation(summary="List all supported countries with their ISO code and name for the profile.")
	@SecurityRequirement(name = "JWT")
	public Map<String,String> getValidCountries() {
		return profileService.getValidCountries();
	}
	
	@GET
	@Path("/languages")
	@PermitAll
	@Produces(MediaTypeConstants.JSON)
	@Operation(summary="List all supported languages with their ISO code and name for the profile.")
	@SecurityRequirement(name = "JWT")
	public Map<String,String> getValidLanguages() {
		return profileService.getValidLanguages();
	}
	
	@POST
	@Consumes(MediaTypeConstants.JSON)
	@Path("/delete")
	@PermitAll
	@Operation(summary="Delete an existing user.")
	@APIResponse(responseCode = "204", description = "Delete successfull.")
	@APIResponse(responseCode = "401", description = "Invalid credentials")
	public Response delete(@Parameter(required=true) @Valid @NotNull UserCredential param) throws ServletException {
		LoginResponse loginResponse = userService.login(param, userAccess.get().getIpAddress()).orElse(null);
		if (loginResponse == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		userService.deleteUser(loginResponse.getId());
		return Response.status(Status.NO_CONTENT).build();
	}
}
