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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import com.bravson.socialalert.UriConstants;
import com.bravson.socialalert.file.media.MediaFileConstants;
import com.bravson.socialalert.file.store.FileStore;

import lombok.val;

@Path("/" + UriConstants.FILE_SERVICE_URI)
@ManagedBean
@RolesAllowed("user")
public class FileService {

	@Inject
	private FileRepository mediaRepository;

	@Inject
	private FileStore fileStore;

	private Response streamFile(String fileUri, String variantName) throws IOException {
		val fileEntity = mediaRepository.findFile(fileUri).orElse(null);
		if (fileEntity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
        
		val fileFormat = fileEntity.findVariantFormat(variantName).orElse(null);
		if (fileFormat == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		File file = fileStore.getExistingFile(fileEntity.getMd5(), fileEntity.getTimestamp(), fileFormat);
        val response = Response.ok(createStreamingOutput(file), fileFormat.getContentType());
		response.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        response.header("Content-Length", file.length());
		return response.build();
	}
	
	private StreamingOutput createStreamingOutput(File file) {
		return os -> Files.copy(file.toPath(), os);
	}

	@GET
	@Path("/download/{fileUri}")
	public Response download(@PathParam("fileUri") String fileUri) throws IOException {
		return streamFile(fileUri, MediaFileConstants.MEDIA_VARIANT);
	}
	
	@GET
	@Path("/preview/{fileUri}")
	public Response preview(@PathParam("fileUri") String fileUri) throws IOException {
		return streamFile(fileUri, MediaFileConstants.PREVIEW_VARIANT);
	}
	
	@GET
	@Path("/thumbnail/{fileUri}")
	public Response thumbnail(@PathParam("fileUri") String fileUri) throws IOException {
		return streamFile(fileUri, MediaFileConstants.THUMBNAIL_VARIANT);
	}
}
