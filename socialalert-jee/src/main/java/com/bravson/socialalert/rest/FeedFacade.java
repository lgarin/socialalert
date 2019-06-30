package com.bravson.socialalert.rest;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.bravson.socialalert.business.feed.FeedService;
import com.bravson.socialalert.business.user.activity.UserActivity;
import com.bravson.socialalert.domain.feed.FeedItemInfo;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="feed")
@Path("/feed")
@RolesAllowed("user")
@UserActivity
public class FeedFacade {
	
	@Inject
	FeedService feedService;

	@GET
	@Path("/{userId : .+}")
	@Operation(summary="Get the feed for the specified user.")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiResponse(responseCode = "200", description = "The feed has been retrieved successfuly.", content=@Content(schema=@Schema(implementation=FeedItemInfo.class)))
	@ApiResponse(responseCode = "404", description = "No user exists with this id.")
	public QueryResult<FeedItemInfo> getFeed(@Parameter(description="The user id.", required=true) @NotEmpty @PathParam("userId") String userId,
			@Parameter(description="Sets the timestamp in milliseconds since the epoch when the paging started.", required=false) @Min(0) @QueryParam("pagingTimestamp") Long pagingTimestamp,
			@Parameter(description="Sets the page number to return.", required=false) @DefaultValue("0") @Min(0) @QueryParam("pageNumber") int pageNumber,
			@Parameter(description="Sets the size of the page to return.", required=false) @DefaultValue("20") @Min(1) @Max(100) @QueryParam("pageSize")  int pageSize,
			@Parameter(description="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) {
		
		return feedService.getFeed(userId, PagingParameter.of(pagingTimestamp, pageNumber, pageSize));
	}
}
