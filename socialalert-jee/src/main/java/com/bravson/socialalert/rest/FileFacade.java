package com.bravson.socialalert.rest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.bravson.socialalert.business.file.FileReadService;
import com.bravson.socialalert.business.file.FileResponse;
import com.bravson.socialalert.business.file.FileSearchService;
import com.bravson.socialalert.business.file.FileUploadParameter;
import com.bravson.socialalert.business.file.FileUploadService;
import com.bravson.socialalert.business.user.TokenAccess;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.activity.UserActivity;
import com.bravson.socialalert.business.user.avatar.UserAvatarService;
import com.bravson.socialalert.domain.file.FileInfo;
import com.bravson.socialalert.domain.media.format.MediaFileConstants;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.infrastructure.rest.MediaTypeConstants;

@Tag(name="file")
@Path("/file")
@RolesAllowed("user")
@UserActivity
public class FileFacade {
	
	@ConfigProperty(name="file.maxUploadSize")
	long maxUploadSize;
	
	@ConfigProperty(name="file.cacheMaxAge")
	int mediaCacheMaxAge;
	
	@Inject
	@TokenAccess
	Instance<UserAccess> userAccess;
	
	@Context
	HttpServletRequest httpRequest;

	@Context
	UriInfo uriInfo;
	
	@Inject
	FileReadService fileReadService;
	
	@Inject
	FileUploadService fileUploadService;
	
	@Inject
	FileSearchService fileSearchService;
	
	@Inject
	UserAvatarService userAvatarService;
	
	private static StreamingOutput createStreamingOutput(File file) {
		return os -> Files.copy(file.toPath(), os);
	}

	private Response createStreamResponse(FileResponse fileResponse) {
		ResponseBuilder response = Response.ok(createStreamingOutput(fileResponse.getFile()));
		response.header("Content-Disposition", "attachment; filename=\"" + fileResponse.getFile().getName() + "\"");
        response.header("Content-Length", fileResponse.getFile().length());
        response.type(fileResponse.getFormat().getContentType());
		return response.build();
	}
	
	@GET
	@Path("/download/{mediaUri : .+}")
	@Operation(summary="Download a file in the same format as it has been uploaded.")
	@APIResponse(responseCode = "200", description = "File will be streamed.")
	@APIResponse(responseCode = "404", description = "No media exists with this uri.")
	public Response download(
			@Parameter(description="The relative file uri.", required=true)
			@NotEmpty @PathParam("mediaUri") String fileUri) throws IOException {
		return createStreamResponse(fileReadService.download(fileUri).orElseThrow(NotFoundException::new));
	}
	
	@PermitAll
	@GET
	@Path("/preview/{mediaUri : .+}")
	@Operation(summary="Download a picture in the preview format.")
	@APIResponse(responseCode = "200", description = "Picture will be streamed.", content= @Content(mediaType=MediaFileConstants.JPG_MEDIA_TYPE))
	@APIResponse(responseCode = "404", description = "No picture exists with this uri.")
	public Response preview(
			@Parameter(description="The relative file uri.", required=true) @NotEmpty @PathParam("mediaUri") String fileUri) throws IOException {
		return createStreamResponse(fileReadService.preview(fileUri).orElseThrow(NotFoundException::new));
	}
	
	@PermitAll
	@GET
	@Path("/stream/{mediaUri : .+}")
	@Operation(summary="Download a video in the preview format.", description="For video media, the preview is initialy a still picture and the video preview is only created after a delay.")
	@APIResponse(responseCode = "200", description = "Video will be streamed.", content= @Content(mediaType=MediaFileConstants.MP4_MEDIA_TYPE))
	@APIResponse(responseCode = "404", description = "No video exists with this uri.")
	public Response stream(
			@Parameter(description="The relative file uri.", required=true) @NotEmpty @PathParam("mediaUri") String fileUri) throws IOException {
		return createStreamResponse(fileReadService.stream(fileUri).orElseThrow(NotFoundException::new));
	}
	
	@GET
	@Produces(MediaTypeConstants.JSON)
	@Operation(summary="Access the file metadata.")
	@SecurityRequirement(name = "JWT")
	@Path("/metadata/{mediaUri : .+}")
	@APIResponse(responseCode = "200", description = "File metadata are available in the response.", content=@Content(schema=@Schema(implementation=FileInfo.class)))
	@APIResponse(responseCode = "404", description = "No media exists with this uri.")
	public FileInfo getMetadata(
			@Parameter(description="The relative file uri.", required=true) @NotEmpty @PathParam("mediaUri") String fileUri) throws IOException {
		return fileSearchService.findFileByUri(fileUri).orElseThrow(NotFoundException::new);
	}
	
	@GET
	@Produces(MediaTypeConstants.JSON)
	@Operation(summary="List the new files for the current user.")
	@SecurityRequirement(name = "JWT")
	@Path("/list/new")
	@APIResponse(responseCode = "200", description = "List of file metadata is available in the response.", content=@Content(schema=@Schema(implementation=FileInfo.class, type=SchemaType.ARRAY)))
	public List<FileInfo> listNewFiles() throws IOException {
		return fileSearchService.findNewFilesByUserId(userAccess.get().getUserId());
	}
	
	@PermitAll
	@GET
	@Path("/thumbnail/{mediaUri : .+}")
	@Produces(MediaFileConstants.JPG_MEDIA_TYPE)
	@Operation(summary="Download a jpeg thumbnail of the media.")
	@APIResponse(responseCode = "200", description = "File will be streamed.")
	@APIResponse(responseCode = "404", description = "No media exists with this uri.")
	public Response thumbnail(
			@Parameter(description="The relative file uri.", required=true) @NotEmpty @PathParam("mediaUri") String fileUri) throws IOException {
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
	@SecurityRequirement(name = "JWT")
    @APIResponse(responseCode = "201", description = "The picture is ready to be claimed.", headers = @Header(name = "Location", description = "The media url", schema = @Schema(type=SchemaType.STRING, format="uri")))
    @APIResponse(responseCode = "413", description = "The file is too large.")
	@APIResponse(responseCode = "415", description = "The media is not in the expected format.")
	public Response uploadPicture(
			@RequestBody(description="The file content must be included in the body of the HTTP request.", required=true) @NotNull File inputFile) throws IOException, ServletException {
		return createUploadResponse(fileUploadService.uploadMedia(createUploadParameter(inputFile), userAccess.get()));
	}
	
	@POST
	@Consumes({MediaFileConstants.MOV_MEDIA_TYPE, MediaFileConstants.MP4_MEDIA_TYPE})
	@Path("/upload/video")
	@Operation(summary="Upload a video file.")
	@SecurityRequirement(name = "JWT")
    @APIResponse(responseCode = "201", description = "The video is ready to be claimed.", headers = @Header(name = "Location", description = "The media url", schema = @Schema(type=SchemaType.STRING, format="uri")))
    @APIResponse(responseCode = "413", description = "The file is too large.")
    @APIResponse(responseCode = "415", description = "The media is not in the expected format.")
	public Response uploadVideo(
		    @RequestBody(description="The file content must be included in the body of the HTTP request.", required=true) @NotNull File inputFile) throws IOException, ServletException {
		return createUploadResponse(fileUploadService.uploadMedia(createUploadParameter(inputFile), userAccess.get()));
	}

	private FileUploadParameter createUploadParameter(File inputFile) {
		if (inputFile.length() > maxUploadSize) {
			throw new WebApplicationException(Status.REQUEST_ENTITY_TOO_LARGE);
		}
		return FileUploadParameter.builder().inputFile(inputFile).contentType(httpRequest.getContentType()).build();
	}
	
	@POST
	@Consumes(MediaFileConstants.JPG_MEDIA_TYPE)
	@Produces(MediaTypeConstants.JSON)
	@Path("/upload/avatar")
	@Operation(summary="Upload a profile picture.")
	@SecurityRequirement(name = "JWT")
    @APIResponse(responseCode = "200", description = "The profile picture has been set.")
    @APIResponse(responseCode = "413", description = "The file is too large.")
	@APIResponse(responseCode = "415", description = "The media is not in the expected format.")
	public UserInfo uploadAvatar(
			@RequestBody(description="The file content must be included in the body of the HTTP request.", required=true) @NotNull File inputFile) throws IOException, ServletException {
		return userAvatarService.storeAvatar(createUploadParameter(inputFile), userAccess.get());
	}

	@PermitAll
	@GET
	@Path("/avatar/small/{imageUri : .+}")
	@Produces(MediaFileConstants.JPG_MEDIA_TYPE)
	@Operation(summary="Download a small profile picture of the specified user.")
	@APIResponse(responseCode = "200", description = "File will be streamed.")
	@APIResponse(responseCode = "404", description = "Specified image could not be found.")
	public Response smallAvatar(
			@Parameter(description="The user id to return", required=true) @NotEmpty @PathParam("imageUri") String imageUri) throws NotFoundException, IOException {
		return createStreamResponse(userAvatarService.getSmallImage(imageUri));
	}
	
	@PermitAll
	@GET
	@Path("/avatar/large/{imageUri : .+}")
	@Produces(MediaFileConstants.JPG_MEDIA_TYPE)
	@Operation(summary="Download a large profile picture of the specified user.")
	@APIResponse(responseCode = "200", description = "File will be streamed.")
	@APIResponse(responseCode = "404", description = "Specified image could not be found.")
	public Response largeAvatar(
			@Parameter(description="The user id to return", required=true) @NotEmpty @PathParam("imageUri") String imageUri) throws NotFoundException, IOException {
		return createStreamResponse(userAvatarService.getLargeImage(imageUri));
	}
}
