package com.bravson.socialalert.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.time.Instant;
import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;

import com.bravson.socialalert.file.media.MediaFileConstants;
import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaFileProcessor;
import com.bravson.socialalert.file.media.MediaMetadata;
import com.bravson.socialalert.file.picture.PictureFileProcessor;
import com.bravson.socialalert.file.store.FileStore;
import com.bravson.socialalert.file.video.VideoFileProcessor;

import lombok.val;

@Path("/file")
@ManagedBean
@RolesAllowed("user")
public class FileService {

	@Resource(name="maxUploadSize")
	Long maxUploadSize;
	
	@Inject
	MediaRepository mediaRepository;
	
	@Inject
	PictureFileProcessor pictureFileProcessor;
	
	@Inject
	VideoFileProcessor videoFileProcessor;
	
	@Inject
	FileStore fileStore;
	
	@Inject
	Principal principal;
	
	@Inject
	Logger logger;

	@POST
	@Consumes(MediaFileConstants.JPG_MEDIA_TYPE)
	@Path("/uploadPicture")
	public Response uploadPicture(File inputFile, @Context HttpServletRequest request) throws IOException, ServletException {
		return uploadMedia(inputFile, request, pictureFileProcessor);
	}
	
	@POST
	@Consumes({MediaFileConstants.MOV_MEDIA_TYPE, MediaFileConstants.MP4_MEDIA_TYPE})
	@Path("/uploadVideo")
	public Response uploadVideo(File inputFile, @Context HttpServletRequest request) throws IOException, ServletException {
		return uploadMedia(inputFile, request, videoFileProcessor);
	}

	private Response uploadMedia(File inputFile, HttpServletRequest request, MediaFileProcessor processor) throws IOException, ServletException {
		if (request.getContentLengthLong() > maxUploadSize) {
			return Response.status(Status.REQUEST_ENTITY_TOO_LARGE).build();
		}
	
		val mediaMetadata = buildMediaMetadata(inputFile, processor).orElse(null);
		if (mediaMetadata == null) {
			return Response.status(Status.UNSUPPORTED_MEDIA_TYPE).build();
		}
		
		val fileFormat = MediaFileFormat.fromMediaContentType(request.getContentType()).orElse(null);
		if (fileFormat == null) {
			return Response.status(Status.UNSUPPORTED_MEDIA_TYPE).build();
		}
		
		val fileMetadata = buildFileMetadata(inputFile, request);
		
		val fileUri = fileStore.buildFileUri(fileMetadata.getMd5(), fileMetadata.getTimestamp(), fileFormat);
		fileStore.storeMedia(inputFile, fileUri);
		val fileEntity = mediaRepository.storeMedia(fileUri, fileFormat, fileMetadata, mediaMetadata);
		
		val previewFile = fileStore.buildFilePath(fileMetadata.getMd5(), fileMetadata.getTimestamp(), processor.getPreviewFormat());
		processor.createPreview(inputFile, previewFile);
		fileEntity.getFileFormats().add(processor.getPreviewFormat());
		
		val thumbnailFile = fileStore.buildFilePath(fileMetadata.getMd5(), fileMetadata.getTimestamp(), processor.getThumbnailFormat()); 
		processor.createThumbnail(inputFile, thumbnailFile);
		fileEntity.getFileFormats().add(processor.getThumbnailFormat());
		
		return Response.created(URI.create("file/download/" + fileUri)).build();
	}
	
	private Optional<MediaMetadata> buildMediaMetadata(File inputFile, MediaFileProcessor processor) throws IOException {
		try {
			return Optional.of(processor.parseMetadata(inputFile));
		} catch (Exception e) {
			logger.info("Cannot extract metadata", e);
			return Optional.empty();
		}
	}
	
	private FileMetadata buildFileMetadata(File file, HttpServletRequest request) throws IOException {
		val builder = FileMetadata.builder();
		builder.md5(fileStore.computeMd5(file));
		builder.timestamp(Instant.now());
		builder.contentType(request.getContentType());
		builder.contentLength(request.getContentLengthLong());
		builder.userId(principal.getName());
		builder.ipAddress(request.getRemoteAddr());
		return builder.build();
	}
	
	private Response streamFile(String fileUri, String variantName) {
		val fileEntity = mediaRepository.findFile(fileUri).orElse(null);
		if (fileEntity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
        
		val fileFormat = fileEntity.findVariantFormat(variantName).orElse(null);
		if (fileFormat == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		File file = fileStore.buildFilePath(fileEntity.getFileMetadata().getMd5(), fileEntity.getFileMetadata().getTimestamp(), fileFormat);
		val stream = fileStore.createStreamingOutput(fileEntity.getFileMetadata().getMd5(), fileEntity.getFileMetadata().getTimestamp(), fileFormat);
        val response = Response.ok(stream, fileEntity.getFileMetadata().getContentType());
		response.header("Content-Disposition", "attachment; filename=\"" + fileUri + "\"");
        response.header("Content-Length", file.length());
		return response.build();
	}

	@GET
	@Path("/download/{fileUri}")
	public Response download(@PathParam("fileUri") String fileUri) {
		return streamFile(fileUri, MediaFileConstants.MEDIA_VARIANT);
	}
	
	@GET
	@Path("/preview/{fileUri}")
	public Response preview(@PathParam("fileUri") String fileUri) {
		return streamFile(fileUri, MediaFileConstants.PREVIEW_VARIANT);
	}
	
	@GET
	@Path("/thumbnail/{fileUri}")
	public Response thumbnail(@PathParam("fileUri") String fileUri) {
		return streamFile(fileUri, MediaFileConstants.THUMBNAIL_VARIANT);
	}
}
