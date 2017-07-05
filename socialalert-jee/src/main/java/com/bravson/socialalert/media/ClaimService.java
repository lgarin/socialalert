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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;

import com.bravson.socialalert.file.FileEntity;
import com.bravson.socialalert.file.FileRepository;
import com.bravson.socialalert.infrastructure.log.Logged;
import com.bravson.socialalert.user.activity.UserActivity;

import lombok.NonNull;

@Path("/media")
@RolesAllowed("user")
@Logged
@UserActivity
public class ClaimService {

	@Inject
	FileRepository fileRepository;
	
	@Inject
	MediaRepository mediaRepository;
	
	@Inject
	Principal principal;
	
	@Inject
	Logger logger;
	
	@Inject
	HttpServletRequest request;
	
	@POST
	@Path("/claim/{fileUri : .+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public MediaInfo claimPicture(@NotEmpty @PathParam("fileUri") String fileUri, @Valid @NonNull ClaimPictureParameter parameter) {
		mediaRepository.findMedia(fileUri).ifPresent((m) -> new WebApplicationException(Status.CONFLICT));
		FileEntity fileEntity = fileRepository.findFile(fileUri).orElseThrow(() -> new WebApplicationException(Status.NOT_FOUND));
		if (!fileEntity.getUserId().equals(principal.getName())) {
			throw new WebApplicationException(Status.FORBIDDEN);
		}
		MediaEntity mediaEntity = mediaRepository.storeMedia(fileEntity, parameter, principal.getName(), request.getRemoteAddr());
		return mediaEntity.toMediaInfo(); // TODO fill UserInfo
		
	}
}
