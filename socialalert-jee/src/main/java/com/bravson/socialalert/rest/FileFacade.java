package com.bravson.socialalert.rest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.security.Principal;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.hibernate.validator.constraints.NotEmpty;

import com.bravson.socialalert.business.file.FileMetadata;
import com.bravson.socialalert.business.file.FileResponse;
import com.bravson.socialalert.business.file.FileService;
import com.bravson.socialalert.business.file.FileUploadParameter;
import com.bravson.socialalert.business.file.FileUploadService;
import com.bravson.socialalert.business.file.media.MediaFileConstants;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.activity.UserActivity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

@Api(tags={"file"})
@Path("/file")
@RolesAllowed("user")
@UserActivity
public class FileFacade {
	
	@Resource(name="maxUploadSize")
	long maxUploadSize;
	
	@Resource(name="mediaCacheMaxAge")
	int mediaCacheMaxAge;
	
	@Inject
	Principal principal;
	
	@Inject
	HttpServletRequest httpRequest;

	@Context
	Request request;
	
	@Context
	UriInfo uriInfo;
	
	@Inject
	FileService fileService;
	
	@Inject
	FileUploadService fileUploadService;
	
	private static StreamingOutput createStreamingOutput(File file) {
		return os -> Files.copy(file.toPath(), os);
	}

	private Response createStreamResponse(FileResponse fileResponse) {
		EntityTag entityTag = new EntityTag(fileResponse.getFormat().name());
		ResponseBuilder response = request.evaluatePreconditions(entityTag);
		if (response == null) {
			response = Response.ok(createStreamingOutput(fileResponse.getFile()));
			response.tag(entityTag);
			response.header("Content-Disposition", "attachment; filename=\"" + fileResponse.getFile().getName() + "\"");
	        response.header("Content-Length", fileResponse.getFile().length());
	        response.type(fileResponse.getFormat().getContentType());
		}
        
        if (fileResponse.isTemporary()) {
        	CacheControl cacheControl = new CacheControl();
        	cacheControl.setMaxAge(mediaCacheMaxAge);
        	response.cacheControl(cacheControl);
        }
		return response.build();
	}
	
	@GET
	@Path("/download/{fileUri : .+}")
	@ApiOperation(value="Download a file in the same format as it has been uploaded.")
	@ApiResponses(value= {
			@ApiResponse(code = 200, message = "File will be streamed."),
			@ApiResponse(code = 404, message = "No media exists with this uri.") })
	public Response download(
			@ApiParam(value="The relative file uri.", required=true)
			@NotEmpty @PathParam("fileUri") String fileUri) throws IOException {
		return createStreamResponse(fileService.download(fileUri).orElseThrow(NotFoundException::new));
	}
	
	@GET
	@Path("/preview/{fileUri : .+}")
	@ApiOperation(value="Download a file in the preview format.",
		produces=MediaFileConstants.JPG_MEDIA_TYPE + " or " + MediaFileConstants.MP4_MEDIA_TYPE, 
		notes="For video media, the preview is initialy a still picture and the video preview is only created after a delay.")
	@ApiResponses(value= {
			@ApiResponse(code = 200, message = "File will be streamed."),
			@ApiResponse(code = 404, message = "No media exists with this uri.") })
	public Response preview(
			@ApiParam(value="The relative file uri.", required=true) @NotEmpty @PathParam("fileUri") String fileUri,
			@ApiParam(value="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) throws IOException {
		return createStreamResponse(fileService.preview(fileUri).orElseThrow(NotFoundException::new));
	}
	
	@GET
	@Path("/thumbnail/{fileUri : .+}")
	@Produces(MediaFileConstants.JPG_MEDIA_TYPE)
	@ApiOperation(value="Download a jpeg thumbnail of the media.")
	@ApiResponses(value= {
			@ApiResponse(code = 200, message = "File will be streamed."),
			@ApiResponse(code = 404, message = "No media exists with this uri.") })
	public Response thumbnail(
			@ApiParam(value="The relative file uri.", required=true) @NotEmpty @PathParam("fileUri") String fileUri,
			@ApiParam(value="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) throws IOException {
		return createStreamResponse(fileService.thumbnail(fileUri).orElseThrow(NotFoundException::new));
	}
	
	private Response createUploadResponse(FileMetadata metadata) {
		URI fileUri = uriInfo.getBaseUriBuilder().path(FileFacade.class).path("download").path(metadata.buildFileUri()).build();
		return Response.created(fileUri).build();
	}
	
	@POST
	@Consumes(MediaFileConstants.JPG_MEDIA_TYPE)
	@Path("/upload/picture")
	@ApiOperation(value="Upload a picture file.", code=201)
	@ApiResponses(value = { 
		      @ApiResponse(code = 201, message = "The picture is ready to be claimed.", 
		                   responseHeaders = @ResponseHeader(name = "Location", description = "The media url", response = URL.class)),
		      @ApiResponse(code = 413, message = "The file is too large."),
		      @ApiResponse(code = 415, message = "The media is not in the expected format.")})
	public Response uploadPicture(
			@ApiParam(value="The file content must be included in the body of the HTTP request.", required=true) @NotNull File inputFile,
			@ApiParam(value="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) throws IOException, ServletException {
		return createUploadResponse(fileUploadService.uploadMedia(createUploadParameter(inputFile), UserAccess.of(principal.getName(), httpRequest.getRemoteAddr())));
	}
	
	@POST
	@Consumes({MediaFileConstants.MOV_MEDIA_TYPE, MediaFileConstants.MP4_MEDIA_TYPE})
	@Path("/upload/video")
	@ApiOperation(value="Upload a video file.", code=201)
	@ApiResponses(value = { 
		      @ApiResponse(code = 201, message = "The video is ready to be claimed.", 
		                   responseHeaders = @ResponseHeader(name = "Location", description = "The media url", response = URL.class)),
		      @ApiResponse(code = 413, message = "The file is too large."),
		      @ApiResponse(code = 415, message = "The media is not in the expected format.")})
	public Response uploadVideo(
			@ApiParam(value="The file content must be included in the body of the HTTP request.", required=true) @NotNull File inputFile,
			@ApiParam(value="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) throws IOException, ServletException {
		return createUploadResponse(fileUploadService.uploadMedia(createUploadParameter(inputFile), UserAccess.of(principal.getName(), httpRequest.getRemoteAddr())));
	}

	private FileUploadParameter createUploadParameter(File inputFile) {
		if (inputFile.length() > maxUploadSize) {
			throw new WebApplicationException(Status.REQUEST_ENTITY_TOO_LARGE);
		}
		return FileUploadParameter.builder().inputFile(inputFile).contentType(httpRequest.getContentType()).build();
	}
}
