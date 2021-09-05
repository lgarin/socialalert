package com.bravson.socialalert.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.bravson.socialalert.business.feed.FeedService;
import com.bravson.socialalert.business.user.activity.UserActivity;
import com.bravson.socialalert.business.user.token.UserAccess;
import com.bravson.socialalert.domain.feed.FeedActivity;
import com.bravson.socialalert.domain.feed.FeedItemInfo;
import com.bravson.socialalert.domain.feed.PeriodicFeedActivityCount;
import com.bravson.socialalert.domain.media.statistic.PeriodInterval;
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
	Instance<UserAccess> userAccess;

	@GET
	@Path("/current")
	@Operation(summary="Get the feed for the current user.")
	@SecurityRequirement(name = "JWT")
	@Produces(MediaTypeConstants.JSON)
	public QueryResult<FeedItemInfo> getFeed(
			@Parameter(description="Define the keywords for searching the feed.", required=false) @QueryParam("keywords") String keywords,
			@Parameter(description="Define the category for searching the feed.", required=false) @QueryParam("category") String category,
			@Parameter(description="Sets the timestamp in milliseconds since the epoch when the paging started.", required=false) @Min(0) @QueryParam("pagingTimestamp") Long pagingTimestamp,
			@Parameter(description="Sets the page number to return.", required=false) @DefaultValue("0") @Min(0) @QueryParam("pageNumber") int pageNumber,
			@Parameter(description="Sets the size of the page to return.", required=false) @DefaultValue("20") @Min(1) @Max(100) @QueryParam("pageSize")  int pageSize) {
		
		return feedService.getFeed(userAccess.get().getUserId(), category, keywords, PagingParameter.of(pagingTimestamp, pageNumber, pageSize));
	}
	
	@GET
	@Path("/userHistogram/{userId : .+}")
	@Operation(summary="Get the activity histogram for the specified user.")
	@SecurityRequirement(name = "JWT")
	@Produces(MediaTypeConstants.JSON)
	public List<PeriodicFeedActivityCount> groupUserActivitiesByPeriod(
			@Parameter(description="The user id.", required=true) @NotEmpty @PathParam("userId") String userId, 
			@Parameter(description="Define the type of activity.", required=true) @NotNull @QueryParam("activity") FeedActivity activity, 
			@Parameter(description="Define the period interval.", required=false) @DefaultValue("HOUR") @QueryParam("interval") PeriodInterval interval) {
		return feedService.groupUserActivitiesByPeriod(userId, activity, interval);
	}
	
	@GET
	@Path("/mediaHistogram/{mediaUri : .+}")
	@Operation(summary="Get the activity histogram for the specified media.")
	@SecurityRequirement(name = "JWT")
	@Produces(MediaTypeConstants.JSON)
	public List<PeriodicFeedActivityCount> groupMediaActivitiesByPeriod(
			@Parameter(description="The relative media uri.", required=true) @NotEmpty @PathParam("mediaUri") String mediaUri, 
			@Parameter(description="Define the type of activity.", required=true) @NotNull @QueryParam("activity") FeedActivity activity, 
			@Parameter(description="Define the period interval.", required=false) @DefaultValue("HOUR") @QueryParam("interval") PeriodInterval interval) {
		return feedService.groupMediaActivitiesByPeriod(mediaUri, activity, interval);
	}
}
