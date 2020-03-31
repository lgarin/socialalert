package com.bravson.socialalert.rest;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.bravson.socialalert.business.feed.FeedService;
import com.bravson.socialalert.business.user.TokenAccess;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.activity.UserActivity;
import com.bravson.socialalert.domain.feed.FeedItemInfo;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.infrastructure.rest.MediaTypeConstants;

@Tag(name="feed")
@Path("/feed")
@RolesAllowed("user")
@UserActivity
public class FeedFacade {
	
	@Inject
	FeedService feedService;
	
	@Inject
	@TokenAccess
	Instance<UserAccess> userAccess;

	@GET
	@Path("/current")
	@Operation(summary="Get the feed for the current user.")
	@SecurityRequirement(name = "JWT")
	@Produces(MediaTypeConstants.JSON)
	@APIResponse(responseCode = "200", description = "The feed has been retrieved successfuly.", content=@Content(schema=@Schema(implementation=FeedItemInfo.class)))
	public QueryResult<FeedItemInfo> getFeed(
			@Parameter(description="Sets the timestamp in milliseconds since the epoch when the paging started.", required=false) @Min(0) @QueryParam("pagingTimestamp") Long pagingTimestamp,
			@Parameter(description="Sets the page number to return.", required=false) @DefaultValue("0") @Min(0) @QueryParam("pageNumber") int pageNumber,
			@Parameter(description="Sets the size of the page to return.", required=false) @DefaultValue("20") @Min(1) @Max(100) @QueryParam("pageSize")  int pageSize) {
		
		return feedService.getFeed(userAccess.get().getUserId(), PagingParameter.of(pagingTimestamp, pageNumber, pageSize));
	}
}
