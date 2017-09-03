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
import lombok.NonNull;

@Api
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
	public MediaInfo claimPicture(@NotEmpty @PathParam("fileUri") String fileUri, @Valid @NonNull ClaimPictureParameter parameter) {
		return mediaClaimService.claimPicture(toMediaClaimParameter(fileUri), parameter);
	}
	
	private MediaClaimParameter toMediaClaimParameter(String fileUri) {
		return MediaClaimParameter.builder().fileUri(fileUri).userId(principal.getName()).ipAddress(httpRequest.getRemoteAddr()).build();
	}
}
