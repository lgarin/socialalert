package com.bravson.socialalert.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaSizeVariant;
import com.bravson.socialalert.file.store.FileStore;
import com.bravson.socialalert.infrastructure.log.Logged;
import com.bravson.socialalert.user.activity.UserActivity;

@Path("/" + UriConstants.FILE_SERVICE_URI)
@RolesAllowed("user")
@Logged
@UserActivity
public class FileService {

	@Resource(name="mediaCacheMaxAge")
	int mediaCacheMaxAge;
	
	@Inject
	FileRepository mediaRepository;

	@Inject
	FileStore fileStore;
	
	@Context
	Request request;

	private Response streamFile(String fileUri, MediaSizeVariant sizeVariant) throws IOException {
		FileEntity fileEntity = mediaRepository.findFile(fileUri).orElse(null);
		if (fileEntity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
        
		MediaFileFormat fileFormat = fileEntity.findVariantFormat(sizeVariant).orElse(null);
		if (fileFormat == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		FileMetadata fileMetadata = fileEntity.getMediaFileMetadata();
		File file = fileStore.getExistingFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), fileFormat);
       
		return createResponse(fileFormat, file, fileEntity.isTemporary(fileFormat));
	}

	private Response createResponse(MediaFileFormat fileFormat, File file, boolean disableCaching) {
		EntityTag entityTag = new EntityTag(fileFormat.name());
		ResponseBuilder response = request.evaluatePreconditions(entityTag);
		if (response == null) {
			response = Response.ok(createStreamingOutput(file), fileFormat.getContentType());
			response.tag(entityTag);
			response.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
	        response.header("Content-Length", file.length());
	        response.type(fileFormat.getContentType());
		}
        
        if (!disableCaching) {
        	CacheControl cacheControl = new CacheControl();
        	cacheControl.setMaxAge(mediaCacheMaxAge);
        	response.cacheControl(cacheControl);
        }
		return response.build();
	}
	
	private StreamingOutput createStreamingOutput(File file) {
		return os -> Files.copy(file.toPath(), os);
	}

	@GET
	@Path("/download/{fileUri : .+}")
	public Response download(@NotEmpty @PathParam("fileUri") String fileUri) throws IOException {
		return streamFile(fileUri, MediaSizeVariant.MEDIA);
	}
	
	@GET
	@Path("/preview/{fileUri : .+}")
	public Response preview(@NotEmpty @PathParam("fileUri") String fileUri) throws IOException {
		return streamFile(fileUri, MediaSizeVariant.PREVIEW);
	}
	
	@GET
	@Path("/thumbnail/{fileUri : .+}")
	public Response thumbnail(@NotEmpty @PathParam("fileUri") String fileUri) throws IOException {
		return streamFile(fileUri, MediaSizeVariant.THUMBNAIL);
	}
}
