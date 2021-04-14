package com.bravson.socialalert.business.file.video;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;

import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;

import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.file.entity.FileMetadata;
import com.bravson.socialalert.business.file.entity.FileRepository;
import com.bravson.socialalert.business.file.media.AsyncMediaProcessedEvent;
import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.infrastructure.async.AsyncRepository;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
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
	
	@Transactional(value = TxType.NEVER)
	public void onAsyncEvent(@ObservesAsync AsyncVideoPreviewEvent event) {
		try {
			fileRepository.findFile(event.getFileUri()).ifPresent(this::createPreview);
		} catch (Exception e) {
			logger.error("Cannot process video for " + event.getFileUri(), e);
		}
	}
	
	private void createPreview(FileEntity fileEntity) {
		try {
			FileMetadata fileMetadata = fileEntity.getFileMetadata();
			File inputFile = fileStore.getExistingFile(fileMetadata.getMd5(), fileMetadata.getFormattedDate(), fileMetadata.getFileFormat());
			File previewFile = fileStore.createEmptyFile(fileMetadata.getMd5(), fileMetadata.getFormattedDate(), videoFileProcessor.getPreviewFormat());
			videoFileProcessor.createPreview(inputFile, previewFile);
			// use new transaction from repository
			fileRepository.addVariant(fileEntity.getId(), buildFileMetadata(previewFile, videoFileProcessor.getPreviewFormat()));
			asyncRepository.fireAsync(AsyncMediaProcessedEvent.of(fileEntity.getId()));
		} catch (IOException e) {
			throw new UncheckedIOException("Cannot create preview video", e);
		}
	}
	
	private FileMetadata buildFileMetadata(File file, MediaFileFormat fileFormat) throws IOException {
		return FileMetadata.builder()
			.md5(fileStore.computeMd5Hex(file))
			.timestamp(Instant.now())
			.contentSize(file.length())
			.fileFormat(fileFormat)
			.build();
	}
}
