package com.bravson.socialalert.rest;

import java.time.Duration;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.bravson.socialalert.business.media.MediaConstants;
import com.bravson.socialalert.business.media.MediaSearchService;
import com.bravson.socialalert.business.media.MediaService;
import com.bravson.socialalert.business.media.MediaUpsertService;
import com.bravson.socialalert.business.media.SearchMediaParameter;
import com.bravson.socialalert.business.media.UpsertMediaParameter;
import com.bravson.socialalert.business.media.comment.MediaCommentService;
import com.bravson.socialalert.business.user.TokenAccess;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.activity.UserActivity;
import com.bravson.socialalert.domain.location.GeoArea;
import com.bravson.socialalert.domain.location.GeoBox;
import com.bravson.socialalert.domain.location.GeoStatistic;
import com.bravson.socialalert.domain.media.MediaDetail;
import com.bravson.socialalert.domain.media.MediaInfo;
import com.bravson.socialalert.domain.media.MediaKind;
import com.bravson.socialalert.domain.media.comment.MediaCommentDetail;
import com.bravson.socialalert.domain.media.comment.MediaCommentInfo;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.domain.user.approval.ApprovalModifier;
import com.bravson.socialalert.infrastructure.rest.MediaTypeConstants;

@Tag(name="media")
@Path("/media")
@RolesAllowed("user")
@UserActivity
public class MediaFacade {

	@Inject
	@TokenAccess
	Instance<UserAccess> userAccess;
	
	@Inject
	MediaUpsertService mediaUpsertService;
	
	@Inject
	MediaSearchService mediaSearchService;
	
	@Inject
	MediaService mediaService;
	
	@Inject
	MediaCommentService commentService;
	
	@POST
	@Path("/claim/{mediaUri : .+}")
	@Consumes(MediaTypeConstants.JSON)
	@Produces(MediaTypeConstants.JSON)
	@Operation(summary="Claim a media which has been uploaded recently.")
	@SecurityRequirement(name = "JWT")
	@APIResponse(responseCode = "200", description = "This media has been successfully claimed.", content=@Content(schema=@Schema(implementation=MediaInfo.class)))
	@APIResponse(responseCode = "403", description = "This media does not belong to the current user.")
	@APIResponse(responseCode = "404", description = "No picture exists with this uri.")
	@APIResponse(responseCode = "409", description = "This media exists has already been claimed.")
	public MediaInfo claimMedia(
			@Parameter(description="The relative file uri.", required=true) @NotEmpty @PathParam("mediaUri") String fileUri,
			@Valid @NotNull UpsertMediaParameter parameter) {
		return mediaUpsertService.claimMedia(fileUri, parameter, userAccess.get());
	}
	
	@POST
	@Path("/update/{mediaUri : .+}")
	@Consumes(MediaTypeConstants.JSON)
	@Produces(MediaTypeConstants.JSON)
	@Operation(summary="Update the meta information about a media.")
	@SecurityRequirement(name = "JWT")
	@APIResponse(responseCode = "200", description = "The metainformation have been successfully updated.", content=@Content(schema=@Schema(implementation=MediaInfo.class)))
	@APIResponse(responseCode = "403", description = "This media does not belong to the current user.")
	@APIResponse(responseCode = "404", description = "No media exists with this uri.")
	public MediaInfo updateMedia(
			@Parameter(description="The relative media uri.", required=true) @NotEmpty @PathParam("mediaUri") String mediaUri,
			@Valid @NotNull UpsertMediaParameter parameter) {
		return mediaUpsertService.updateMedia(mediaUri, parameter, userAccess.get());
	}
	
	@PermitAll
	@GET
	@Path("/search")
	@Produces(MediaTypeConstants.JSON)
	@Operation(summary="Search claimed media based on any combination of the provided parameters.")
	@SecurityRequirement(name = "JWT")
	public QueryResult<MediaInfo> searchMedia(
			@Parameter(description="Restrict the type of returned media.", required=false) @QueryParam("kind") MediaKind mediaKind,
			@Parameter(description="Define the area for the returned media.", required=false) @QueryParam("minLatitude") Double minLatitude,
			@Parameter(description="Define the area for the returned media.", required=false) @QueryParam("maxLatitude") Double maxLatitude,
			@Parameter(description="Define the area for the returned media.", required=false) @QueryParam("minLongitude") Double minLongitude,
			@Parameter(description="Define the area for the returned media.", required=false) @QueryParam("maxLongitude") Double maxLongitude,
			@Parameter(description="Define the keywords for searching the media.", required=false) @QueryParam("keywords") String keywords,
			@Parameter(description="Define the maximum age in milliseconds of the returned media.", required=false) @Min(0) @QueryParam("maxAge") Long maxAge,
			@Parameter(description="Define the category for searching the media.", required=false) @QueryParam("category") String category,
			@Parameter(description="Define the user id of the creator for searching the media.", required=false) @QueryParam("creator") String creator,
			@Parameter(description="Sets the timestamp in milliseconds since the epoch when the paging started.", required=false) @Min(0) @QueryParam("pagingTimestamp") Long pagingTimestamp,
			@Parameter(description="Sets the page number to return.", required=false) @DefaultValue("0") @Min(0) @QueryParam("pageNumber") int pageNumber,
			@Parameter(description="Sets the size of the page to return.", required=false) @DefaultValue("20") @Min(1) @Max(100) @QueryParam("pageSize")  int pageSize) {
		
		SearchMediaParameter parameter = new SearchMediaParameter();
		if (mediaKind != null) {
			parameter.setMediaKind(mediaKind);
		}
		if (minLatitude != null || maxLatitude != null || minLongitude != null || maxLongitude != null) {
			if (minLatitude == null || maxLatitude == null || minLongitude == null || maxLongitude == null) {
				throw new BadRequestException("minLatitude, maxLatitude, minLongitude and maxLongitude must be set together");
			}
			parameter.setArea(GeoBox.builder().minLat(minLatitude).maxLat(maxLatitude).minLon(minLongitude).maxLon(maxLongitude).build());
		}
		if (keywords != null) {
			parameter.setKeywords(keywords);
		}
		if (maxAge != null) {
			parameter.setMaxAge(Duration.ofMillis(maxAge));
		}
		if (category != null) {
			parameter.setCategory(category);
		}
		if (creator != null) {
			parameter.setCreator(creator);
		}
		return mediaSearchService.searchMedia(parameter, PagingParameter.of(pagingTimestamp, pageNumber, pageSize));
	}
	
	@GET
	@Path("/searchNearest")
	@Produces(MediaTypeConstants.JSON)
	@Operation(summary="Search claimed media based on any combination of the provided parameters.")
	@SecurityRequirement(name = "JWT")
	public QueryResult<MediaInfo> searchNearestMedia(
			@Parameter(description="Define the location for the nearest media.", required=true) @QueryParam("latitude") Double latitude,
			@Parameter(description="Define the location for the nearest media.", required=true) @QueryParam("longitude") Double longitude,
			@Parameter(description="Define the maximum distance in kilometer for the nearest media.", required=true) @QueryParam("maxDistance") Double maxDistance,
			@Parameter(description="Restrict the type of returned media.", required=false) @QueryParam("kind") MediaKind mediaKind,
			@Parameter(description="Define the keywords for searching the media.", required=false) @QueryParam("keywords") String keywords,
			@Parameter(description="Define the maximum age in milliseconds of the returned media.", required=false) @Min(0) @QueryParam("maxAge") Long maxAge,
			@Parameter(description="Define the category for searching the media.", required=false) @QueryParam("category") String category,
			@Parameter(description="Define the user id of the creator for searching the media.", required=false) @QueryParam("creator") String creator,
			@Parameter(description="Sets the timestamp in milliseconds since the epoch when the paging started.", required=false) @Min(0) @QueryParam("pagingTimestamp") Long pagingTimestamp,
			@Parameter(description="Sets the page number to return.", required=false) @DefaultValue("0") @Min(0) @QueryParam("pageNumber") int pageNumber,
			@Parameter(description="Sets the size of the page to return.", required=false) @DefaultValue("20") @Min(1) @Max(100) @QueryParam("pageSize")  int pageSize) {
		
		SearchMediaParameter parameter = new SearchMediaParameter();
		if (mediaKind != null) {
			parameter.setMediaKind(mediaKind);
		}
		if (keywords != null) {
			parameter.setKeywords(keywords);
		}
		if (maxAge != null) {
			parameter.setMaxAge(Duration.ofMillis(maxAge));
		}
		if (category != null) {
			parameter.setCategory(category);
		}
		if (creator != null) {
			parameter.setCreator(creator);
		}
		parameter.setLocation(GeoArea.builder().latitude(latitude).longitude(longitude).radius(maxDistance).build());
		return mediaSearchService.searchMedia(parameter, PagingParameter.of(pagingTimestamp, pageNumber, pageSize));
	}
	
	@GET
	@Path("/view/{mediaUri : .+}")
	@Produces(MediaTypeConstants.JSON)
	@Operation(summary="View the media details. If it is the first call for this media in the user session, then the hit count will be increased by one.")
	@SecurityRequirement(name = "JWT")
	@APIResponse(responseCode = "200", description = "The detail is available in the response.", content=@Content(schema=@Schema(implementation=MediaDetail.class)))
	@APIResponse(responseCode = "404", description = "No media exists with this uri.")
	public MediaDetail viewMediaDetail(
			@Parameter(description="The relative media uri.", required=true) @NotEmpty @PathParam("mediaUri") String mediaUri) {
		
		return mediaService.viewMediaDetail(mediaUri, userAccess.get().getUserId());
	}
	
	@POST
	@Path("/approval/like/{mediaUri : .+}")
	@Produces(MediaTypeConstants.JSON)
	@Operation(summary="Set the approval modifier of the media to 'like'.")
	@SecurityRequirement(name = "JWT")
	@APIResponse(responseCode = "200", description = "The new status is available in the response.", content=@Content(schema=@Schema(implementation=MediaDetail.class)))
	@APIResponse(responseCode = "404", description = "No media exists with this uri.")
	public MediaDetail likeMedia(
			@Parameter(description="The relative media uri.", required=true) @NotEmpty @PathParam("mediaUri") String mediaUri) {
		return mediaService.setApprovalModifier(mediaUri, ApprovalModifier.LIKE, userAccess.get().getUserId());
	}
	
	@POST
	@Path("/approval/dislike/{mediaUri : .+}")
	@Produces(MediaTypeConstants.JSON)
	@Operation(summary="Set the approval modifier of the media to 'dislike'.")
	@SecurityRequirement(name = "JWT")
	@APIResponse(responseCode = "200", description = "The new status is available in the response.", content=@Content(schema=@Schema(implementation=MediaDetail.class)))
	@APIResponse(responseCode = "404", description = "No media exists with this uri.")
	public MediaDetail dislikeMedia(
			@Parameter(description="The relative media uri.", required=true) @NotEmpty @PathParam("mediaUri") String mediaUri) {
		return mediaService.setApprovalModifier(mediaUri, ApprovalModifier.DISLIKE, userAccess.get().getUserId());
	}
	
	@POST
	@Path("/feeling/{mediaUri : .+}/{feeling}")
	@Produces(MediaTypeConstants.JSON)
	@Operation(summary="Set the use feeling for this media.")
	@SecurityRequirement(name = "JWT")
	@APIResponse(responseCode = "200", description = "The new feeling is available in the response.", content=@Content(schema=@Schema(implementation=MediaDetail.class)))
	@APIResponse(responseCode = "404", description = "No media exists with this uri.")
	public MediaDetail setFeeling(
			@Parameter(description="The relative media uri.", required=true) @NotEmpty @PathParam("mediaUri") String mediaUri,
			@Parameter(description="The relative media uri.", required=true) @Min(-MediaConstants.MAX_ABS_FEELING) @Max(MediaConstants.MAX_ABS_FEELING) @PathParam("feeling") Integer feeling) {
		return mediaService.setFeeling(mediaUri, feeling, userAccess.get().getUserId());
	}
	
	@POST
	@Path("/approval/reset/{mediaUri : .+}")
	@Operation(summary="Set the approval modifier of the media to 'null'.")
	@SecurityRequirement(name = "JWT")
	@Produces(MediaTypeConstants.JSON)
	@APIResponse(responseCode = "200", description = "The new status is available in the response.", content=@Content(schema=@Schema(implementation=MediaDetail.class)))
	@APIResponse(responseCode = "404", description = "No media exists with this uri.")
	public MediaDetail resetMediaApproval(
			@Parameter(description="The relative media uri.", required=true) @NotEmpty @PathParam("mediaUri") String mediaUri) {
		return mediaService.setApprovalModifier(mediaUri, null, userAccess.get().getUserId());
	}
	
	@POST
	@Path("/comment/{mediaUri : .+}")
	@Operation(summary="Comment the specified media.")
	@SecurityRequirement(name = "JWT")
	@Consumes(MediaTypeConstants.TEXT_PLAIN)
	@Produces(MediaTypeConstants.JSON)
	@APIResponse(responseCode = "200", description = "The comment has been created.", content=@Content(schema=@Schema(implementation=MediaCommentInfo.class)))
	@APIResponse(responseCode = "404", description = "No media exists with this uri.")
	public MediaCommentInfo commentMedia(
			@Parameter(description="The relative media uri.", required=true) @NotEmpty @PathParam("mediaUri") String mediaUri,
			@Parameter(description="The comment text.", required=true) @NotEmpty @Size(max=MediaConstants.MAX_COMMENT_LENGTH) String comment) {
		return commentService.createComment(mediaUri, comment, userAccess.get());
	}
	
	@POST
	@Path("/comment/approval/like/{commentId : .+}")
	@Produces(MediaTypeConstants.JSON)
	@Operation(summary="Set the approval modifier of the comment to 'like'.")
	@SecurityRequirement(name = "JWT")
	@APIResponse(responseCode = "200", description = "The new status is available in the response.", content=@Content(schema=@Schema(implementation=MediaDetail.class)))
	@APIResponse(responseCode = "404", description = "No comment exists with this id.")
	public MediaCommentDetail likeComment(
			@Parameter(description="The comment id.", required=true) @NotEmpty @PathParam("commentId") String commentId) {
		return commentService.setApprovalModifier(commentId, ApprovalModifier.LIKE, userAccess.get().getUserId());
	}
	
	@POST
	@Path("/comment/approval/dislike/{commentId : .+}")
	@Produces(MediaTypeConstants.JSON)
	@Operation(summary="Set the approval modifier of the comment to 'dislike'.")
	@SecurityRequirement(name = "JWT")
	@APIResponse(responseCode = "200", description = "The new status is available in the response.", content=@Content(schema=@Schema(implementation=MediaDetail.class)))
	@APIResponse(responseCode = "404", description = "No comment exists with this id.")
	public MediaCommentDetail dislikeComment(
			@Parameter(description="The comment id.", required=true) @NotEmpty @PathParam("commentId") String commentId) {
		return commentService.setApprovalModifier(commentId, ApprovalModifier.DISLIKE, userAccess.get().getUserId());
	}
	
	@POST
	@Path("/comment/approval/reset/{commentId : .+}")
	@Produces(MediaTypeConstants.JSON)
	@Operation(summary="Set the approval modifier of the comment to 'null'.")
	@SecurityRequirement(name = "JWT")
	@APIResponse(responseCode = "200", description = "The new status is available in the response.", content=@Content(schema=@Schema(implementation=MediaDetail.class)))
	@APIResponse(responseCode = "404", description = "No comment exists with this id.")
	public MediaCommentDetail resetCommentApproval(
			@Parameter(description="The comment id.", required=true) @NotEmpty @PathParam("commentId") String commentId) {
		return commentService.setApprovalModifier(commentId, null, userAccess.get().getUserId());
	}
	
	@GET
	@Path("/comments/{mediaUri : .+}")
	@Produces(MediaTypeConstants.JSON)
	@Operation(summary="List the comments for the specified media.")
	@SecurityRequirement(name = "JWT")
	@APIResponse(responseCode = "200", description = "The matching comments.")
	@APIResponse(responseCode = "404", description = "No media exists with this uri.")
	public QueryResult<MediaCommentDetail> listComments(@Parameter(description="The relative media uri.", required=true) @NotEmpty @PathParam("mediaUri") String mediaUri,
			@Parameter(description="Sets the timestamp in milliseconds since the epoch when the paging started.", required=false) @Min(0) @QueryParam("pagingTimestamp") Long pagingTimestamp,
			@Parameter(description="Sets the page number to return.", required=false) @DefaultValue("0") @Min(0) @QueryParam("pageNumber") int pageNumber,
			@Parameter(description="Sets the size of the page to return.", required=false) @DefaultValue("20") @Min(1) @Max(100) @QueryParam("pageSize")  int pageSize) {
		
		return commentService.listMediaComments(mediaUri, userAccess.get().getUserId(), PagingParameter.of(pagingTimestamp, pageNumber, pageSize));
	}
	
	@GET
	@Path("/mapCount")
	@Produces(MediaTypeConstants.JSON)
	@Operation(summary="Group count claimed media in the given area based on their geo hash.")
	@APIResponse(responseCode = "200", description = "The statistic for each geo hash in the given area.")
	@SecurityRequirement(name = "JWT")
	public List<GeoStatistic> mapMediaMatchCount(@Parameter(description="Restrict the type of returned media.", required=false) @QueryParam("kind") MediaKind mediaKind,
			@Parameter(description="Define the area for the returned media.", required=true) @QueryParam("minLatitude") @NotNull Double minLatitude,
			@Parameter(description="Define the area for the returned media.", required=true) @QueryParam("maxLatitude") @NotNull Double maxLatitude,
			@Parameter(description="Define the area for the returned media.", required=true) @QueryParam("minLongitude") @NotNull Double minLongitude,
			@Parameter(description="Define the area for the returned media.", required=true) @QueryParam("maxLongitude") @NotNull Double maxLongitude,
			@Parameter(description="Define the keywords for searching the media.", required=false) @QueryParam("keywords") String keywords,
			@Parameter(description="Define the maximum age in milliseconds of the returned media.", required=false) @Min(0) @QueryParam("maxAge") Long maxAge,
			@Parameter(description="Define the category for searching the media.", required=false) @QueryParam("category") String category,
			@Parameter(description="Define the user id of the creator for searching the media.", required=false) @QueryParam("creator") String creator) {
		
		SearchMediaParameter parameter = new SearchMediaParameter();
		if (mediaKind != null) {
			parameter.setMediaKind(mediaKind);
		}
		
		parameter.setArea(GeoBox.builder().minLat(minLatitude).maxLat(maxLatitude).minLon(minLongitude).maxLon(maxLongitude).build());
		
		if (keywords != null) {
			parameter.setKeywords(keywords);
		}
		if (maxAge != null) {
			parameter.setMaxAge(Duration.ofMillis(maxAge));
		}
		if (category != null) {
			parameter.setCategory(category);
		}
		if (creator != null) {
			parameter.setCreator(creator);
		}
		return mediaSearchService.groupByGeoHash(parameter);
	}
	
	@GET
	@Path("/suggestTags")
	@Produces(MediaTypeConstants.JSON)
	@Operation(summary="Suggest some tags based on the given term.")
	@APIResponse(responseCode = "200", description = "The list of tags.")
	@SecurityRequirement(name = "JWT")
	public List<String> suggestTags(@Parameter(description="Define the term for searching the tags.", required=true) @QueryParam("term") String term,
			@Parameter(description="Define the maximum number of matching tags to be returned.", required=true) @QueryParam("maxHitCount") int maxHitCount) {
		return mediaSearchService.suggestTags(term, maxHitCount);
	}
}
