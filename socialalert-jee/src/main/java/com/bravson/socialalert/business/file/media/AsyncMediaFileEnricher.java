package com.bravson.socialalert.business.file.media;

import java.io.File;

import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;

import com.bravson.socialalert.business.file.FileMetadata;
import com.bravson.socialalert.business.file.FileRepository;
import com.bravson.socialalert.business.file.MediaFileStore;
import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.business.file.video.AsyncVideoPreviewEvent;
import com.bravson.socialalert.domain.media.format.MediaSizeVariant;
import com.bravson.socialalert.infrastructure.async.AsyncRepository;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class AsyncMediaFileEnricher {

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
	
	@Inject
	Logger logger;
	
	public void onAsyncEvent(@ObservesAsync AsyncMediaEnrichEvent event) {
		fileRepository.findFile(event.getFileUri()).ifPresent(this::addMetadata);
	}
	
	private void addMetadata(FileEntity fileEntity) {
		
		try {
			FileMetadata fileMetadata = fileEntity.getFileMetadata();
			File inputFile = fileStore.getExistingFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), fileMetadata.getFileFormat());
			
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
			
		} catch (Exception e) {
			logger.error("Cannot process media " + fileEntity.getId(), e);
			// TODO add to error queue
		}
		
	}
	
	
}
