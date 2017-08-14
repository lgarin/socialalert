package com.bravson.socialalert.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotSupportedException;

import org.slf4j.Logger;

import com.bravson.socialalert.UriConstants;
import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaFileProcessor;
import com.bravson.socialalert.file.media.MediaMetadata;
import com.bravson.socialalert.file.picture.PictureFileProcessor;
import com.bravson.socialalert.file.store.FileStore;
import com.bravson.socialalert.file.video.AsyncVideoPreviewEvent;
import com.bravson.socialalert.file.video.SnapshotVideoFileProcessor;
import com.bravson.socialalert.infrastructure.async.AsyncRepository;
import com.bravson.socialalert.infrastructure.log.Logged;

import lombok.NonNull;

@ManagedBean
@Transactional
@Logged
public class FileUploadService {
	
	@Resource(name="maxUploadSize")
	long maxUploadSize;
	
	@Inject
	FileRepository mediaRepository;
	
	@Inject
	PictureFileProcessor pictureFileProcessor;
	
	@Inject
	SnapshotVideoFileProcessor videoFileProcessor;
	
	@Inject
	FileStore fileStore;
	
	@Inject
	AsyncRepository asyncRepository;
	
	@Inject
	Logger logger;

	public URI uploadPicture(@NonNull FileUploadParameter parameter) throws IOException {
		return uploadMedia(parameter, pictureFileProcessor);
	}

	public URI uploadVideo(@NonNull FileUploadParameter parameter) throws IOException {
		return uploadMedia(parameter, videoFileProcessor);
	}

	private URI createDownloadUri(FileMetadata fileMetadata) {
		return URI.create(UriConstants.FILE_SERVICE_URI + "/download/" + fileMetadata.buildFileUri());
	}
	
	private URI uploadMedia(FileUploadParameter parameter, MediaFileProcessor processor) throws IOException {
	
		MediaMetadata mediaMetadata = buildMediaMetadata(parameter.getInputFile(), processor).orElseThrow(NotSupportedException::new);
		MediaFileFormat fileFormat = MediaFileFormat.fromMediaContentType(parameter.getContentType()).orElseThrow(NotSupportedException::new);
		
		FileMetadata fileMetadata = buildFileMetadata(parameter.getInputFile(), fileFormat, parameter.getUserId(), parameter.getIpAddress());
		
		if (mediaRepository.findFile(fileMetadata.buildFileUri()).isPresent()) {
			return createDownloadUri(fileMetadata);
		}
		
		fileStore.storeMedia(parameter.getInputFile(), fileMetadata.getMd5(), fileMetadata.getTimestamp(), fileMetadata.getFileFormat());
		FileEntity fileEntity = mediaRepository.storeMedia(fileMetadata, mediaMetadata);
		
		File thumbnailFile = fileStore.createEmptyFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), processor.getThumbnailFormat()); 
		processor.createThumbnail(parameter.getInputFile(), thumbnailFile);
		fileEntity.addVariant(buildFileMetadata(thumbnailFile, processor.getThumbnailFormat(), parameter.getUserId(), parameter.getIpAddress()));
		
		File previewFile = fileStore.createEmptyFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), processor.getPreviewFormat());
		processor.createPreview(parameter.getInputFile(), previewFile);
		fileEntity.addVariant(buildFileMetadata(previewFile, processor.getPreviewFormat(), parameter.getUserId(), parameter.getIpAddress()));
		
		if (fileMetadata.isVideo()) {
			asyncRepository.fireAsync(AsyncVideoPreviewEvent.of(fileEntity.getId()));
		}
		
		return createDownloadUri(fileMetadata);
	}
	
	private Optional<MediaMetadata> buildMediaMetadata(File inputFile, MediaFileProcessor processor) throws IOException {
		try {
			return Optional.of(processor.parseMetadata(inputFile));
		} catch (Exception e) {
			logger.info("Cannot extract metadata", e);
			return Optional.empty();
		}
	}
	
	private FileMetadata buildFileMetadata(File file, MediaFileFormat fileFormat, String userId, String ipAddress) throws IOException {
		String md5 = fileStore.computeMd5Hex(file);
		return FileMetadata.builder()
			.md5(md5)
			.timestamp(Instant.now())
			.contentLength(file.length())
			.userId(userId)
			.ipAddress(ipAddress)
			.fileFormat(fileFormat)
			.build();
	}
}
