package com.bravson.socialalert.business.file;

import java.io.IOException;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotSupportedException;

import org.slf4j.Logger;

import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.domain.file.FileInfo;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.domain.media.format.MediaSizeVariant;
import com.bravson.socialalert.infrastructure.entity.NewEntity;
import com.bravson.socialalert.infrastructure.layer.Service;
import com.bravson.socialalert.infrastructure.rest.ConflictException;

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
	UserInfoService userService;
	
	@Inject
	@NewEntity
	Event<FileEntity> newFileEvent;
	
	@Inject
	Logger logger;

	private static FileInfo handleExistingFile(FileEntity file, UserAccess userAccess) {
		if (!file.markUploaded(userAccess)) {
			throw new ConflictException();
		}
		return file.toFileInfo();
	}
	
	public FileInfo uploadMedia(@NonNull FileUploadParameter parameter, @NonNull UserAccess userAccess) throws IOException {
		MediaFileFormat fileFormat = determineFileFormat(parameter);
		
		FileMetadata fileMetadata = mediaFileStore.buildFileMetadata(parameter.getInputFile(), fileFormat);
		
		FileEntity existingEntity = mediaRepository.findFile(fileMetadata.buildFileUri()).orElse(null);
		if (existingEntity != null) {
			return userService.fillUserInfo(handleExistingFile(existingEntity, userAccess));
		}
		
		mediaFileStore.storeVariant(parameter.getInputFile(), fileMetadata, MediaSizeVariant.MEDIA);
		FileEntity fileEntity = mediaRepository.storeMedia(fileMetadata, userAccess);
		newFileEvent.fire(fileEntity);
		
		return userService.fillUserInfo(fileEntity.toFileInfo());
	}


	private MediaFileFormat determineFileFormat(FileUploadParameter parameter) {
		/*
		String detectedContentType = Files.probeContentType(parameter.getInputFile().toPath());
		MediaFileFormat detectedFileFormat = MediaFileFormat.fromMediaContentType(detectedContentType).orElseThrow(NotSupportedException::new);
		*/
		MediaFileFormat fileFormat = MediaFileFormat.fromMediaContentType(parameter.getContentType()).orElseThrow(NotSupportedException::new);
		/*
		if (detectedFileFormat != fileFormat) {
			throw new NotSupportedException();
		}
		*/
		return fileFormat;
	}
}
