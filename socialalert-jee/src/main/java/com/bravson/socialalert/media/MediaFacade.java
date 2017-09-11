package com.bravson.socialalert.media;

import java.security.Principal;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.validator.constraints.NotEmpty;

import com.bravson.socialalert.user.activity.UserActivity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(tags= {"media"})
@Path("/media")
@RolesAllowed("user")
@UserActivity
public class MediaFacade {

	@Inject
	Principal principal;
	
	@Inject
	HttpServletRequest httpRequest;
	
	@Inject
	MediaClaimService mediaClaimService;
	
	@Inject
	MediaSearchService mediaSearchService;
	
	@POST
	@Path("/claim/{fileUri : .+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Claim a media which has been uploaded recently.")
	@ApiResponses(value= {
			@ApiResponse(code = 200, message = "File will be streamed."),
			@ApiResponse(code = 403, message = "This media does not belong to the current user."),
			@ApiResponse(code = 404, message = "No picture exists with this uri."),
			@ApiResponse(code = 409, message = "This media exists has already been claimed.")})
	public MediaInfo claimMedia(
			@ApiParam(value="The relative file uri.", required=true) @NotEmpty @PathParam("fileUri") String fileUri,
			@Valid @NotNull ClaimMediaParameter parameter,
			@ApiParam(value="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) {
		return mediaClaimService.claimMedia(toClaimFileParameter(fileUri), parameter);
	}
	
	private ClaimFileParameter toClaimFileParameter(String fileUri) {
		return ClaimFileParameter.builder().fileUri(fileUri).userId(principal.getName()).ipAddress(httpRequest.getRemoteAddr()).build();
	}
	
	@GET
	@Path("/search")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Search claimed media based on any combination of the provided parameters.")
	public QueryResult<MediaInfo> searchMedia(
			@ApiParam(value="The search parameters.", required=true) @Valid @NotNull SearchMediaParameter parameter,
			@ApiParam(value="Used for paging the results.", required=true) @Valid @NotNull PagingParameter paging,
			@ApiParam(value="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) {
		return mediaSearchService.searchMedia(parameter, paging);
	}
}
