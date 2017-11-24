package com.bravson.socialalert.business.file;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotSupportedException;

import org.slf4j.Logger;

import com.bravson.socialalert.business.file.media.MediaFileFormat;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.file.media.MediaSizeVariant;
import com.bravson.socialalert.business.file.video.AsyncVideoPreviewEvent;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.domain.file.FileInfo;
import com.bravson.socialalert.infrastructure.async.AsyncRepository;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Service
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class FileUploadService {
	
	@Inject
	FileRepository mediaRepository;
	
	@Inject
	MediaFileStore mediaFileStore;
	
	@Inject
	AsyncRepository asyncRepository;
	
	@Inject
	UserInfoService userService;
	
	@Inject
	Logger logger;

	public FileInfo uploadMedia(@NonNull FileUploadParameter parameter, @NonNull UserAccess userAccess) throws IOException {
		MediaFileFormat fileFormat = MediaFileFormat.fromMediaContentType(parameter.getContentType()).orElseThrow(NotSupportedException::new);
		MediaMetadata mediaMetadata = buildMediaMetadata(parameter.getInputFile(), fileFormat).orElseThrow(NotSupportedException::new);
		
		FileMetadata fileMetadata = mediaFileStore.buildFileMetadata(parameter.getInputFile(), fileFormat);
		
		Optional<FileEntity> existingEntity = mediaRepository.findFile(fileMetadata.buildFileUri());
		if (existingEntity.isPresent()) {
			return userService.fillUserInfo(existingEntity.get().toFileInfo());
		}
		
		FileEntity newEntity = storeNewFile(parameter.getInputFile(), fileMetadata, mediaMetadata, userAccess);
		return userService.fillUserInfo(newEntity.toFileInfo());
	}

	private FileEntity storeNewFile(File inputFile, FileMetadata fileMetadata, MediaMetadata mediaMetadata, UserAccess userAccess) throws IOException {
		mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.MEDIA);
		FileEntity fileEntity = mediaRepository.storeMedia(fileMetadata, mediaMetadata, userAccess);
		
		FileMetadata thumbnailMetadata = mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.THUMBNAIL);
		fileEntity.addVariant(thumbnailMetadata);
		
		FileMetadata previewMetadata = mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.PREVIEW);
		fileEntity.addVariant(previewMetadata);
		
		if (fileMetadata.isVideo()) {
			asyncRepository.fireAsync(AsyncVideoPreviewEvent.of(fileEntity.getId()));
		}
		
		return fileEntity;
	}

	private Optional<MediaMetadata> buildMediaMetadata(File inputFile, MediaFileFormat fileFormat) throws IOException {
		try {
			return Optional.of(mediaFileStore.buildMediaMetadata(inputFile, fileFormat));
		} catch (Exception e) {
			logger.info("Cannot extract metadata", e);
			return Optional.empty();
		}
	}
}
