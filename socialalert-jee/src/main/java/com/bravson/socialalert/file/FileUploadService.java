package com.bravson.socialalert.file;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotSupportedException;

import org.slf4j.Logger;

import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaMetadata;
import com.bravson.socialalert.file.media.MediaSizeVariant;
import com.bravson.socialalert.file.video.AsyncVideoPreviewEvent;
import com.bravson.socialalert.infrastructure.async.AsyncRepository;
import com.bravson.socialalert.infrastructure.layer.Service;
import com.bravson.socialalert.user.UserAccess;
import com.bravson.socialalert.user.profile.ProfileEntity;
import com.bravson.socialalert.user.profile.ProfileRepository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Service
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class FileUploadService {
	
	@Inject
	FileRepository mediaRepository;
	
	@Inject
	MediaFileStore mediaFileStore;
	
	@Inject
	AsyncRepository asyncRepository;
	
	@Inject
	ProfileRepository profileRepository;
	
	@Inject
	Logger logger;

	public FileMetadata uploadMedia(@NonNull FileUploadParameter parameter, @NonNull UserAccess userAccess) throws IOException {
		MediaFileFormat fileFormat = MediaFileFormat.fromMediaContentType(parameter.getContentType()).orElseThrow(NotSupportedException::new);
		MediaMetadata mediaMetadata = buildMediaMetadata(parameter.getInputFile(), fileFormat).orElseThrow(NotSupportedException::new);
		
		FileMetadata fileMetadata = mediaFileStore.buildFileMetadata(parameter.getInputFile(), fileFormat);
		
		if (mediaRepository.findFile(fileMetadata.buildFileUri()).isPresent()) {
			return fileMetadata;
		}
		
		storeNewFile(parameter.getInputFile(), fileMetadata, mediaMetadata, userAccess);
		
		return fileMetadata;
	}

	private FileEntity storeNewFile(File inputFile, FileMetadata fileMetadata, MediaMetadata mediaMetadata, UserAccess userAccess) throws IOException {
		ProfileEntity profileEntity = profileRepository.findByUserId(userAccess.getUserId()).orElseThrow(ForbiddenException::new);
		mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.MEDIA);
		FileEntity fileEntity = mediaRepository.storeMedia(fileMetadata, mediaMetadata, profileEntity, userAccess);
		
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
