package com.bravson.socialalert.rest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.hibernate.validator.constraints.NotEmpty;

import com.bravson.socialalert.business.file.FileReadService;
import com.bravson.socialalert.business.file.FileResponse;
import com.bravson.socialalert.business.file.FileSearchService;
import com.bravson.socialalert.business.file.FileUploadParameter;
import com.bravson.socialalert.business.file.FileUploadService;
import com.bravson.socialalert.business.file.media.MediaFileConstants;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.activity.UserActivity;
import com.bravson.socialalert.domain.file.FileInfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="file")
@Path("/file")
@RolesAllowed("user")
@UserActivity
public class FileFacade {
	
	@Resource(name="maxUploadSize")
	long maxUploadSize;
	
	@Resource(name="mediaCacheMaxAge")
	int mediaCacheMaxAge;
	
	@Inject
	UserAccess userAccess;
	
	@Inject
	HttpServletRequest httpRequest;

	@Context
	Request request;
	
	@Context
	UriInfo uriInfo;
	
	@Inject
	FileReadService fileReadService;
	
	@Inject
	FileUploadService fileUploadService;
	
	@Inject
	FileSearchService fileSearchService;
	
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
	@Operation(summary="Download a file in the same format as it has been uploaded.")
	@ApiResponse(responseCode = "200", description = "File will be streamed.")
	@ApiResponse(responseCode = "404", description = "No media exists with this uri.")
	public Response download(
			@Parameter(description="The relative file uri.", required=true)
			@NotEmpty @PathParam("fileUri") String fileUri) throws IOException {
		return createStreamResponse(fileReadService.download(fileUri).orElseThrow(NotFoundException::new));
	}
	
	@PermitAll
	@GET
	@Path("/preview/{fileUri : .+}")
	@Operation(summary="Download a file in the preview format.",
			description="For video media, the preview is initialy a still picture and the video preview is only created after a delay.")
	@ApiResponse(responseCode = "200", description = "File will be streamed.", content= {@Content(mediaType=MediaFileConstants.JPG_MEDIA_TYPE), @Content(mediaType=MediaFileConstants.MP4_MEDIA_TYPE)})
	@ApiResponse(responseCode = "404", description = "No media exists with this uri.")
	public Response preview(
			@Parameter(description="The relative file uri.", required=true) @NotEmpty @PathParam("fileUri") String fileUri) throws IOException {
		return createStreamResponse(fileReadService.preview(fileUri).orElseThrow(NotFoundException::new));
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary="Access the file metadata.")
	@Path("/metadata/{fileUri : .+}")
	@ApiResponse(responseCode = "200", description = "File metadata are available in the response.", content=@Content(schema=@Schema(implementation=FileInfo.class)))
	@ApiResponse(responseCode = "404", description = "No media exists with this uri.")
	public FileInfo getMetadata(
			@Parameter(description="The relative file uri.", required=true) @NotEmpty @PathParam("fileUri") String fileUri,
			@Parameter(description="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) throws IOException {
		return fileSearchService.findFileByUri(fileUri).orElseThrow(NotFoundException::new);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary="List the new files for the current user.")
	@Path("/list/new")
	@ApiResponse(responseCode = "200", description = "List of file metadata is available in the response.", content=@Content(array=@ArraySchema(schema=@Schema(implementation=FileInfo.class))))
	public List<FileInfo> listNewFiles(
			@Parameter(description="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) throws IOException {
		return fileSearchService.findNewFilesByUserId(userAccess.getUserId());
	}
	
	@PermitAll
	@GET
	@Path("/thumbnail/{fileUri : .+}")
	@Produces(MediaFileConstants.JPG_MEDIA_TYPE)
	@Operation(summary="Download a jpeg thumbnail of the media.")
	@ApiResponse(responseCode = "200", description = "File will be streamed.")
	@ApiResponse(responseCode = "404", description = "No media exists with this uri.")
	public Response thumbnail(
			@Parameter(description="The relative file uri.", required=true) @NotEmpty @PathParam("fileUri") String fileUri) throws IOException {
		return createStreamResponse(fileReadService.thumbnail(fileUri).orElseThrow(NotFoundException::new));
	}
	
	private Response createUploadResponse(FileInfo fileInfo) {
		URI fileUri = uriInfo.getBaseUriBuilder().path(FileFacade.class).path("download").path(fileInfo.getFileUri()).build();
		return Response.created(fileUri).build();
	}
	
	@POST
	@Consumes(MediaFileConstants.JPG_MEDIA_TYPE)
	@Path("/upload/picture")
	@Operation(summary="Upload a picture file.")
    @ApiResponse(responseCode = "201", description = "The picture is ready to be claimed.", 
    				headers = @Header(name = "Location", description = "The media url", schema = @Schema(type="string", format="uri")))
    @ApiResponse(responseCode = "413", description = "The file is too large.")
	@ApiResponse(responseCode = "415", description = "The media is not in the expected format.")
	public Response uploadPicture(
			@RequestBody(description="The file content must be included in the body of the HTTP request.", required=true) @NotNull File inputFile,
			@Parameter(description="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) throws IOException, ServletException {
		return createUploadResponse(fileUploadService.uploadMedia(createUploadParameter(inputFile), userAccess));
	}
	
	@POST
	@Consumes({MediaFileConstants.MOV_MEDIA_TYPE, MediaFileConstants.MP4_MEDIA_TYPE})
	@Path("/upload/video")
	@Operation(summary="Upload a video file.")
    @ApiResponse(responseCode = "201", description = "The video is ready to be claimed.", 
		                   headers = @Header(name = "Location", description = "The media url", schema = @Schema(type="string", format="uri")))
    @ApiResponse(responseCode = "413", description = "The file is too large.")
    @ApiResponse(responseCode = "415", description = "The media is not in the expected format.")
	public Response uploadVideo(
		    @RequestBody(description="The file content must be included in the body of the HTTP request.", required=true) @NotNull File inputFile,
			@Parameter(description="The authorization token returned by the login function.", required=true) @NotEmpty @HeaderParam("Authorization") String authorization) throws IOException, ServletException {
		return createUploadResponse(fileUploadService.uploadMedia(createUploadParameter(inputFile), userAccess));
	}

	private FileUploadParameter createUploadParameter(File inputFile) {
		if (inputFile.length() > maxUploadSize) {
			throw new WebApplicationException(Status.REQUEST_ENTITY_TOO_LARGE);
		}
		return FileUploadParameter.builder().inputFile(inputFile).contentType(httpRequest.getContentType()).build();
	}
}
