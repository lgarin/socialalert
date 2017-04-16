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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;

import com.bravson.socialalert.UriConstants;
import com.bravson.socialalert.file.media.MediaFileConstants;
import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaFileProcessor;
import com.bravson.socialalert.file.media.MediaMetadata;
import com.bravson.socialalert.file.picture.PictureFileProcessor;
import com.bravson.socialalert.file.store.FileStore;
import com.bravson.socialalert.file.video.VideoFileProcessor;

import lombok.val;

@Path("/upload")
@ManagedBean
@RolesAllowed("user")
public class UploadService {
	@Resource(name="maxUploadSize")
	private long maxUploadSize;
	
	@Inject
	private FileRepository mediaRepository;
	
	@Inject
	private PictureFileProcessor pictureFileProcessor;
	
	@Inject
	private VideoFileProcessor videoFileProcessor;
	
	@Inject
	private FileStore fileStore;
	
	@Inject
	private Principal principal;
	
	@Inject
	private Logger logger;

	@POST
	@Consumes(MediaFileConstants.JPG_MEDIA_TYPE)
	@Path("/picture")
	public Response uploadPicture(File inputFile, @Context HttpServletRequest request) throws IOException, ServletException {
		return uploadMedia(inputFile, request, pictureFileProcessor);
	}
	
	@POST
	@Consumes({MediaFileConstants.MOV_MEDIA_TYPE, MediaFileConstants.MP4_MEDIA_TYPE})
	@Path("/video")
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
		
		val fileMetadata = buildFileMetadata(inputFile, fileFormat, request);
		
		fileStore.storeMedia(inputFile, fileMetadata.getMd5(), fileMetadata.getTimestamp(), fileFormat);
		val fileEntity = mediaRepository.storeMedia(fileMetadata, mediaMetadata);
		
		// TODO delay preview creation
		val previewFile = fileStore.createEmptyFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), processor.getPreviewFormat());
		processor.createPreview(inputFile, previewFile);
		fileEntity.addVariant(buildFileMetadata(previewFile, processor.getPreviewFormat(), request));
		
		val thumbnailFile = fileStore.createEmptyFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), processor.getThumbnailFormat()); 
		processor.createThumbnail(inputFile, thumbnailFile);
		fileEntity.addVariant(buildFileMetadata(thumbnailFile, processor.getThumbnailFormat(), request));
		
		return Response.created(URI.create(UriConstants.FILE_SERVICE_URI + "/" + fileEntity.getFileUri())).build();
	}
	
	private Optional<MediaMetadata> buildMediaMetadata(File inputFile, MediaFileProcessor processor) throws IOException {
		try {
			return Optional.of(processor.parseMetadata(inputFile));
		} catch (Exception e) {
			logger.info("Cannot extract metadata", e);
			return Optional.empty();
		}
	}
	
	private FileMetadata buildFileMetadata(File file, MediaFileFormat fileFormat, HttpServletRequest request) throws IOException {
		return FileMetadata.builder()
			.md5(fileStore.computeMd5Hex(file))
			.timestamp(Instant.now())
			.contentType(request.getContentType())
			.contentLength(request.getContentLengthLong())
			.userId(principal.getName())
			.ipAddress(request.getRemoteAddr())
			.fileFormat(fileFormat)
			.build();
	}
}
