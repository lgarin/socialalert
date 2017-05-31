package com.bravson.socialalert.media;

import java.util.Collection;

import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.hibernate.validator.constraints.NotEmpty;

import com.bravson.socialalert.infrastructure.log.Logged;

@Path("/media")
@RolesAllowed("user")
@Logged
public class ClaimService {

	@POST
	@Path("/claim/{fileUri : .+}")
	public MediaInfo claimPicture(@FormParam("title") @NotEmpty @Size(max=MediaConstants.MAX_TITLE_LENGTH) String title, @FormParam("location") GeoAddress location, @FormParam("categories") @NotNull Collection<String> categories, @FormParam("tags") @NotNull @Size(max=MediaConstants.MAX_TAG_COUNT) Collection<String> tags) {
		return null;
		
	}
}
