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
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotSupportedException;
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
import com.bravson.socialalert.file.video.AsyncVideoPreviewEvent;
import com.bravson.socialalert.file.video.SnapshotVideoFileProcessor;
import com.bravson.socialalert.infrastructure.async.AsyncRepository;
import com.bravson.socialalert.infrastructure.log.Logged;

@Path("/upload")
@ManagedBean
@RolesAllowed("user")
@Logged
public class UploadService {
	@Resource(name="maxUploadSize")
	private long maxUploadSize;
	
	@Inject
	private FileRepository mediaRepository;
	
	@Inject
	private PictureFileProcessor pictureFileProcessor;
	
	@Inject
	private SnapshotVideoFileProcessor videoFileProcessor;
	
	@Inject
	private FileStore fileStore;
	
	@Inject
	private AsyncRepository asyncRepository;
	
	@Inject
	private Principal principal;
	
	@Inject
	private Logger logger;

	@POST
	@Consumes(MediaFileConstants.JPG_MEDIA_TYPE)
	@Path("/picture")
	public Response uploadPicture(@NotNull File inputFile, @Context HttpServletRequest request) throws IOException, ServletException {
		return uploadMedia(inputFile, request, pictureFileProcessor);
	}
	
	@POST
	@Consumes({MediaFileConstants.MOV_MEDIA_TYPE, MediaFileConstants.MP4_MEDIA_TYPE})
	@Path("/video")
	public Response uploadVideo(@NotNull File inputFile, @Context HttpServletRequest request) throws IOException, ServletException {
		return uploadMedia(inputFile, request, videoFileProcessor);
	}

	private URI createDownloadUri(FileMetadata fileMetadata) {
		return URI.create(UriConstants.FILE_SERVICE_URI + "/download/" + fileMetadata.buildFileUri());
	}
	
	private Response uploadMedia(File inputFile, HttpServletRequest request, MediaFileProcessor processor) throws IOException, ServletException {
		if (request.getContentLengthLong() > maxUploadSize) {
			return Response.status(Status.REQUEST_ENTITY_TOO_LARGE).build();
		}
	
		MediaMetadata mediaMetadata = buildMediaMetadata(inputFile, processor).orElseThrow(NotSupportedException::new);
		MediaFileFormat fileFormat = MediaFileFormat.fromMediaContentType(request.getContentType()).orElseThrow(NotSupportedException::new);
		
		FileMetadata fileMetadata = buildFileMetadata(inputFile, fileFormat, request);
		
		if (mediaRepository.findFile(fileMetadata.buildFileUri()).isPresent()) {
			return Response.status(Status.CREATED).location(createDownloadUri(fileMetadata)).build();
		}
		
		fileStore.storeMedia(inputFile, fileMetadata.getMd5(), fileMetadata.getTimestamp(), fileFormat);
		FileEntity fileEntity = mediaRepository.storeMedia(fileMetadata, mediaMetadata);
		
		File thumbnailFile = fileStore.createEmptyFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), processor.getThumbnailFormat()); 
		processor.createThumbnail(inputFile, thumbnailFile);
		fileEntity.addVariant(buildFileMetadata(thumbnailFile, processor.getThumbnailFormat(), request));
		
		File previewFile = fileStore.createEmptyFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), processor.getPreviewFormat());
		processor.createPreview(inputFile, previewFile);
		fileEntity.addVariant(buildFileMetadata(previewFile, processor.getPreviewFormat(), request));
		
		asyncRepository.fireAsync(new AsyncVideoPreviewEvent(fileEntity.getFileUri()));
		
		return Response.created(createDownloadUri(fileMetadata)).build();
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
			.contentLength(file.length())
			.userId(principal.getName())
			.ipAddress(request.getRemoteAddr())
			.fileFormat(fileFormat)
			.build();
	}
}
