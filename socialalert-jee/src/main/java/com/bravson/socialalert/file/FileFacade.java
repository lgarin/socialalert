package com.bravson.socialalert.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;
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
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.hibernate.validator.constraints.NotEmpty;

import com.bravson.socialalert.UriConstants;
import com.bravson.socialalert.file.media.MediaFileConstants;
import com.bravson.socialalert.user.activity.UserActivity;

@Path("/" + UriConstants.FILE_SERVICE_URI)
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
			response = Response.ok(createStreamingOutput(fileResponse.getFile()), fileResponse.getFormat().getContentType());
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
	public Response download(@NotEmpty @PathParam("fileUri") String fileUri) throws IOException {
		return createStreamResponse(fileService.download(fileUri).orElseThrow(NotFoundException::new));
	}
	
	@GET
	@Path("/preview/{fileUri : .+}")
	public Response preview(@NotEmpty @PathParam("fileUri") String fileUri) throws IOException {
		return createStreamResponse(fileService.preview(fileUri).orElseThrow(NotFoundException::new));
	}
	
	@GET
	@Path("/thumbnail/{fileUri : .+}")
	public Response thumbnail(@NotEmpty @PathParam("fileUri") String fileUri) throws IOException {
		return createStreamResponse(fileService.thumbnail(fileUri).orElseThrow(NotFoundException::new));
	}
	
	private Response createUploadResponse(FileMetadata metadata) {
		URI fileUri = URI.create(UriConstants.FILE_SERVICE_URI + "/download/" + metadata.buildFileUri());
		return Response.created(fileUri).build();
	}
	
	@POST
	@Consumes(MediaFileConstants.JPG_MEDIA_TYPE)
	@Path("/upload/picture")
	public Response uploadPicture(@NotNull File inputFile) throws IOException, ServletException {
		return createUploadResponse(fileUploadService.uploadMedia(createUploadParameter(inputFile)));
	}
	
	@POST
	@Consumes({MediaFileConstants.MOV_MEDIA_TYPE, MediaFileConstants.MP4_MEDIA_TYPE})
	@Path("/upload/video")
	public Response uploadVideo(@NotNull File inputFile) throws IOException, ServletException {
		return createUploadResponse(fileUploadService.uploadMedia(createUploadParameter(inputFile)));
	}

	private FileUploadParameter createUploadParameter(File inputFile) {
		if (inputFile.length() > maxUploadSize) {
			throw new WebApplicationException(Status.REQUEST_ENTITY_TOO_LARGE);
		}
		return FileUploadParameter.builder().inputFile(inputFile).contentType(httpRequest.getContentType()).userId(principal.getName()).ipAddress(httpRequest.getRemoteAddr()).build();
	}
}
