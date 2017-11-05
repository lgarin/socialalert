package com.bravson.socialalert.media;

import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.hibernate.validator.constraints.NotEmpty;

import com.bravson.socialalert.domain.approval.ApprovalModifier;
import com.bravson.socialalert.domain.location.GeoBox;
import com.bravson.socialalert.domain.location.GeoStatistic;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.media.comment.MediaCommentInfo;
import com.bravson.socialalert.media.comment.MediaCommentService;
import com.bravson.socialalert.user.UserAccess;
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
	MediaUpsertService mediaUpsertService;
	
	@Inject
	MediaSearchService mediaSearchService;
	
	@Inject
	MediaService mediaService;
	
	@Inject
	MediaCommentService commentService;
	
	@POST
	@Path("/claim/{fileUri : .+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Claim a media which has been uploaded recently.")
	@ApiResponses(value= {
			@ApiResponse(code = 200, message = "This media has been successfully claimed."),
			@ApiResponse(code = 403, message = "This media does not belong to the current user."),
			@ApiResponse(code = 404, message = "No picture exists with this uri."),
			@ApiResponse(code = 409, message = "This media exists has already been claimed.")})
	public MediaInfo claimMedia(
			@ApiParam(value="The relative file uri.", required=true) @NotEmpty @PathParam("fileUri") String fileUri,
			@Valid @NotNull UpsertMediaParameter parameter,
			@ApiParam(value="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) {
		return mediaUpsertService.claimMedia(fileUri, parameter, UserAccess.of(principal.getName(), httpRequest.getRemoteAddr()));
	}
	
	@POST
	@Path("/update/{mediaUri : .+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Update the meta information about a media.")
	@ApiResponses(value= {
			@ApiResponse(code = 200, message = "The metainformation have been successfully updated."),
			@ApiResponse(code = 403, message = "This media does not belong to the current user."),
			@ApiResponse(code = 404, message = "No media exists with this uri.")})
	public MediaInfo updateMedia(
			@ApiParam(value="The relative media uri.", required=true) @NotEmpty @PathParam("mediaUri") String mediaUri,
			@Valid @NotNull UpsertMediaParameter parameter,
			@ApiParam(value="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) {
		return mediaUpsertService.updateMedia(mediaUri, parameter, UserAccess.of(principal.getName(), httpRequest.getRemoteAddr()));
	}
	
	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Search claimed media based on any combination of the provided parameters.")
	public QueryResult<MediaInfo> searchMedia(
			@ApiParam(value="Restrict the type of returned media.", required=false) @QueryParam("kind") MediaKind mediaKind,
			@ApiParam(value="Define the area for the returned media.", required=false) @QueryParam("minLatitude") Double minLatitude,
			@ApiParam(value="Define the area for the returned media.", required=false) @QueryParam("maxLatitude") Double maxLatitude,
			@ApiParam(value="Define the area for the returned media.", required=false) @QueryParam("minLongitude") Double minLongitude,
			@ApiParam(value="Define the area for the returned media.", required=false) @QueryParam("maxLongitude") Double maxLongitude,
			@ApiParam(value="Define the keywords for searching the media.", required=false) @QueryParam("keywords") String keywords,
			@ApiParam(value="Define the maximum age in milliseconds of the returned media.", required=false) @Min(0) @QueryParam("maxAge") Long maxAge,
			@ApiParam(value="Define the category for searching the media.", required=false) @QueryParam("category") String category,
			@ApiParam(value="Sets the timestamp in milliseconds since the epoch when the paging started.", required=false) @Min(0) @QueryParam("pagingTimestamp") Long pagingTimestamp,
			@ApiParam(value="Sets the page number to return.", required=false) @DefaultValue("0") @Min(0) @QueryParam("pageNumber") int pageNumber,
			@ApiParam(value="Sets the size of the page to return.", required=false) @DefaultValue("20") @Min(1) @Max(100) @QueryParam("pageSize")  int pageSize,
			@ApiParam(value="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) {
		
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
		if (pagingTimestamp == null) {
			pagingTimestamp = System.currentTimeMillis();
		}
		PagingParameter paging = new PagingParameter(Instant.ofEpochMilli(pagingTimestamp), pageNumber, pageSize);
		return mediaSearchService.searchMedia(parameter, paging);
	}
	
	@GET
	@Path("/view/{mediaUri : .+}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="View the media details. If it is the first call for this media in the user session, then the hit count will be increased by one.")
	@ApiResponses(value= {
			@ApiResponse(code = 200, message = "The detail is available in the response."),
			@ApiResponse(code = 404, message = "No media exists with this uri.")})
	public MediaDetail viewMediaDetail(
			@ApiParam(value="The relative media uri.", required=true) @NotEmpty @PathParam("mediaUri") String mediaUri,
			@ApiParam(value="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) {
		
		return mediaService.viewMediaDetail(mediaUri, principal.getName());
	}
	
	@POST
	@Path("/approval/like/{mediaUri : .+}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Set the approval modifier of the media to 'like'.")
	@ApiResponses(value= {
			@ApiResponse(code = 200, message = "The new status is available in the response."),
			@ApiResponse(code = 404, message = "No media exists with this uri.")})
	public MediaDetail likeMedia(
			@ApiParam(value="The relative media uri.", required=true) @NotEmpty @PathParam("mediaUri") String mediaUri,
			@ApiParam(value="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) {
		return mediaService.setApprovalModifier(mediaUri, ApprovalModifier.LIKE, principal.getName());
	}
	
	@POST
	@Path("/approval/dislike/{mediaUri : .+}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Set the approval modifier of the media to 'dislike'.")
	@ApiResponses(value= {
			@ApiResponse(code = 200, message = "The new status is available in the response."),
			@ApiResponse(code = 404, message = "No media exists with this uri.")})
	public MediaDetail dislikeMedia(
			@ApiParam(value="The relative media uri.", required=true) @NotEmpty @PathParam("mediaUri") String mediaUri,
			@ApiParam(value="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) {
		return mediaService.setApprovalModifier(mediaUri, ApprovalModifier.DISLIKE, principal.getName());
	}
	
	@POST
	@Path("/approval/reset/{mediaUri : .+}")
	@ApiOperation(value="Set the approval modifier of the media to 'null'.")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiResponses(value= {
			@ApiResponse(code = 200, message = "The new status is available in the response."),
			@ApiResponse(code = 404, message = "No media exists with this uri.")})
	public MediaDetail resetMediaApproval(
			@ApiParam(value="The relative media uri.", required=true) @NotEmpty @PathParam("mediaUri") String mediaUri,
			@ApiParam(value="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) {
		return mediaService.setApprovalModifier(mediaUri, null, principal.getName());
	}
	
	@POST
	@Path("/comment/{mediaUri : .+}")
	@ApiOperation(value="Comment the specified media.")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiResponses(value= {
			@ApiResponse(code = 200, message = "The comment has been created."),
			@ApiResponse(code = 404, message = "No media exists with this uri.")})
	public MediaCommentInfo commentMedia(
			@ApiParam(value="The relative media uri.", required=true) @NotEmpty @PathParam("mediaUri") String mediaUri,
			@ApiParam(value="The comment text.", required=true) @NotEmpty @Size(max=MediaConstants.MAX_COMMENT_LENGTH) String comment,
			@ApiParam(value="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) {
		return commentService.createComment(mediaUri, comment, UserAccess.of(principal.getName(), httpRequest.getRemoteAddr()));
	}
	
	@GET
	@Path("/comments/{mediaUri : .+}")
	@ApiOperation(value="List the comments for the specified media.")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiResponses(value= {
			@ApiResponse(code = 200, message = "The comments have been retrieved successfuly."),
			@ApiResponse(code = 404, message = "No media exists with this uri.")})
	public QueryResult<MediaCommentInfo> listComments(@ApiParam(value="The relative media uri.", required=true) @NotEmpty @PathParam("mediaUri") String mediaUri,
			@ApiParam(value="Sets the timestamp in milliseconds since the epoch when the paging started.", required=false) @Min(0) @QueryParam("pagingTimestamp") Long pagingTimestamp,
			@ApiParam(value="Sets the page number to return.", required=false) @DefaultValue("0") @Min(0) @QueryParam("pageNumber") int pageNumber,
			@ApiParam(value="Sets the size of the page to return.", required=false) @DefaultValue("20") @Min(1) @Max(100) @QueryParam("pageSize")  int pageSize,
			@ApiParam(value="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) {
		
		if (pagingTimestamp == null) {
			pagingTimestamp = System.currentTimeMillis();
		}
		PagingParameter paging = new PagingParameter(Instant.ofEpochMilli(pagingTimestamp), pageNumber, pageSize);
		return commentService.listComments(mediaUri, paging);
	}
	
	@GET
	@Path("/mapCount")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Group count claimed media in the given area based on their geo hash.")
	public List<GeoStatistic> mapMediaMatchCount(@ApiParam(value="Restrict the type of returned media.", required=false) @QueryParam("kind") MediaKind mediaKind,
			@ApiParam(value="Define the area for the returned media.", required=true) @QueryParam("minLatitude") @NotNull Double minLatitude,
			@ApiParam(value="Define the area for the returned media.", required=true) @QueryParam("maxLatitude") @NotNull Double maxLatitude,
			@ApiParam(value="Define the area for the returned media.", required=true) @QueryParam("minLongitude") @NotNull Double minLongitude,
			@ApiParam(value="Define the area for the returned media.", required=true) @QueryParam("maxLongitude") @NotNull Double maxLongitude,
			@ApiParam(value="Define the keywords for searching the media.", required=false) @QueryParam("keywords") String keywords,
			@ApiParam(value="Define the maximum age in milliseconds of the returned media.", required=false) @Min(0) @QueryParam("maxAge") Long maxAge,
			@ApiParam(value="Define the category for searching the media.", required=false) @QueryParam("category") String category,
			@ApiParam(value="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) {
		
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
		return mediaSearchService.groupByGeoHash(parameter);
	}
}
