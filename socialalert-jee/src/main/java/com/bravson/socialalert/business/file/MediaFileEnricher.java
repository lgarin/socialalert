package com.bravson.socialalert.business.file;

import java.io.File;
import java.io.IOException;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.file.entity.FileMetadata;
import com.bravson.socialalert.business.file.entity.FileRepository;
import com.bravson.socialalert.business.file.media.AsyncMediaProcessedEvent;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.file.media.MediaMetadataExtractor;
import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.business.file.video.AsyncVideoPreviewEvent;
import com.bravson.socialalert.domain.media.format.MediaSizeVariant;
import com.bravson.socialalert.infrastructure.async.AsyncRepository;
import com.bravson.socialalert.infrastructure.entity.NewEntity;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class MediaFileEnricher {

	@Inject
	FileRepository fileRepository;
	
	@Inject
	FileStore fileStore;
	
	@Inject
	MediaFileStore mediaFileStore;
	
	@Inject
	MediaMetadataExtractor metadataExtractor;
	
	@Inject
	AsyncRepository asyncRepository;
	
	//@VisibleForTesting
	public void handleNewMedia(@Observes @NewEntity FileEntity fileEntity) throws IOException {
		FileMetadata fileMetadata = fileEntity.getFileMetadata();
		File inputFile = fileStore.getExistingFile(fileMetadata.getMd5(), fileMetadata.getFormattedDate(), fileMetadata.getFileFormat());
		
		MediaMetadata mediaMetadata = metadataExtractor.parseMetadata(inputFile);
		if (!fileEntity.markProcessed(mediaMetadata)) {
			throw new IllegalStateException();
		}
		
		FileMetadata thumbnailMetadata = mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.THUMBNAIL);
		fileEntity.addVariant(thumbnailMetadata);
		
		FileMetadata previewMetadata = mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.PREVIEW);
		fileEntity.addVariant(previewMetadata);
		if (fileMetadata.isVideo()) {
			asyncRepository.fireAsync(AsyncVideoPreviewEvent.of(fileEntity.getId()));
		} else {
			asyncRepository.fireAsync(AsyncMediaProcessedEvent.of(fileEntity.getId()));
		}
	}
	
}
