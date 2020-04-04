package com.bravson.socialalert.business.file.video;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;

import com.bravson.socialalert.business.file.FileMetadata;
import com.bravson.socialalert.business.file.FileRepository;
import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.file.media.AsyncMediaProcessedEvent;
import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.infrastructure.async.AsyncRepository;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class AsyncVideoPreviewProcessor {

	@Inject
	FileRepository fileRepository;
	
	@Inject
	FileStore fileStore;
	
	@Inject
	VideoFileProcessor videoFileProcessor;
	
	@Inject
	Logger logger;
	
	@Inject
	AsyncRepository asyncRepository;
	
	public void onAsyncEvent(@ObservesAsync AsyncVideoPreviewEvent event) {
		fileRepository.findFile(event.getFileUri()).ifPresent(this::createPreview);
	}
	
	private void createPreview(FileEntity fileEntity) {
		try {
			FileMetadata fileMetadata = fileEntity.getFileMetadata();
			File inputFile = fileStore.getExistingFile(fileMetadata.getMd5(), fileMetadata.getFormattedDate(), fileMetadata.getFileFormat());
			File previewFile = fileStore.createEmptyFile(fileMetadata.getMd5(), fileMetadata.getFormattedDate(), videoFileProcessor.getPreviewFormat());
			videoFileProcessor.createPreview(inputFile, previewFile);
			fileEntity.addVariant(buildFileMetadata(previewFile, videoFileProcessor.getPreviewFormat(), fileMetadata));
			asyncRepository.fireAsync(AsyncMediaProcessedEvent.of(fileEntity.getId()));
		} catch (IOException e) {
			logger.error("Cannot process video " + fileEntity.getId(), e);
			// TODO add to error queue
		}
	}
	
	private FileMetadata buildFileMetadata(File file, MediaFileFormat fileFormat, FileMetadata inputFileMetadata) throws IOException {
		return FileMetadata.builder()
			.md5(fileStore.computeMd5Hex(file))
			.timestamp(Instant.now())
			.contentSize(file.length())
			.fileFormat(fileFormat)
			.build();
	}
}
