package com.bravson.socialalert.media;

import java.security.Principal;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
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
import io.swagger.annotations.Authorization;
import lombok.NonNull;

@Api(tags= {"media"})
@Path("/media/claim")
@RolesAllowed("user")
@UserActivity
public class MediaFacade {

	@Inject
	Principal principal;
	
	@Inject
	HttpServletRequest httpRequest;
	
	@Inject
	MediaClaimService mediaClaimService;
	
	@POST
	@Path("/picture/{fileUri : .+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Claim a picture which has been uploaded recently.", authorizations= {@Authorization("auth")})
	@ApiResponses(value= {
			@ApiResponse(code = 200, message = "File will be streamed."),
			@ApiResponse(code = 403, message = "This media does not belong to the current user."),
			@ApiResponse(code = 404, message = "No picture exists with this uri."),
			@ApiResponse(code = 409, message = "This media exists has already been claimed.")})
	public MediaInfo claimPicture(@ApiParam(required=true) @NotEmpty @PathParam("fileUri") String fileUri, @Valid @NonNull ClaimPictureParameter parameter) {
		return mediaClaimService.claimPicture(toMediaClaimParameter(fileUri), parameter);
	}
	
	private MediaClaimParameter toMediaClaimParameter(String fileUri) {
		return MediaClaimParameter.builder().fileUri(fileUri).userId(principal.getName()).ipAddress(httpRequest.getRemoteAddr()).build();
	}
}
