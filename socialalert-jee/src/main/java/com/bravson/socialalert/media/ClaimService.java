package com.bravson.socialalert.media;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.hibernate.validator.constraints.NotEmpty;

import com.bravson.socialalert.infrastructure.log.Logged;
import com.bravson.socialalert.user.activity.UserActivity;

@Path("/media")
@RolesAllowed("user")
@Logged
@UserActivity
public class ClaimService {

	@POST
	@Path("/claim/{fileUri : .+}")
	public MediaInfo claimPicture(
				@FormParam("title") @NotEmpty @Size(max=MediaConstants.MAX_TITLE_LENGTH) String title,
				@FormParam("description") @NotEmpty @Size(max=MediaConstants.MAX_DESCRIPTION_LENGTH) String description,
				@FormParam("categories") @NotNull List<String> categories,
				@FormParam("tags") @NotNull @Size(max=MediaConstants.MAX_TAG_COUNT) List<String> tags) {
		return null;
		
	}
}
