package com.bravson.socialalert.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.annotation.ManagedBean;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.hibernate.validator.constraints.NotEmpty;

import javax.ws.rs.core.StreamingOutput;

import com.bravson.socialalert.UriConstants;
import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaSizeVariant;
import com.bravson.socialalert.file.store.FileStore;
import com.bravson.socialalert.infrastructure.log.Logged;

@Path("/" + UriConstants.FILE_SERVICE_URI)
@ManagedBean
@RolesAllowed("user")
@Logged
public class FileService {

	@Inject
	private FileRepository mediaRepository;

	@Inject
	private FileStore fileStore;

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
        ResponseBuilder response = Response.ok(createStreamingOutput(file), fileFormat.getContentType());
		response.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        response.header("Content-Length", file.length());
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
