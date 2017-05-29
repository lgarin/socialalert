package com.bravson.socialalert.file.video;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import javax.annotation.ManagedBean;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.file.FileEntity;
import com.bravson.socialalert.file.FileMetadata;
import com.bravson.socialalert.file.FileRepository;
import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.store.FileStore;
import com.bravson.socialalert.infrastructure.log.Logged;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@ManagedBean
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Logged
public class AsyncVideoPreviewProcessor {

	@Inject
	private FileRepository fileRepository;
	
	@Inject
	private FileStore fileStore;
	
	@Inject
	private VideoFileProcessor videoFileProcessor;
	
	public void onAsyncEvent(@Observes AsyncVideoPreviewEvent event) {
		fileRepository.findFile(event.getFileUri()).ifPresent(this::createPreview);
	}
	
	@SneakyThrows(IOException.class)
	private void createPreview(FileEntity fileEntity) {
		FileMetadata fileMetadata = fileEntity.getMediaFileMetadata();
		File inputFile = fileStore.getExistingFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), fileMetadata.getFileFormat());
		File previewFile = fileStore.createEmptyFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), videoFileProcessor.getPreviewFormat());
		videoFileProcessor.createPreview(inputFile, previewFile);
		fileEntity.addVariant(buildFileMetadata(previewFile, videoFileProcessor.getPreviewFormat(), fileMetadata));
	}
	
	private FileMetadata buildFileMetadata(File file, MediaFileFormat fileFormat, FileMetadata inputFileMetadata) throws IOException {
		return FileMetadata.builder()
			.md5(fileStore.computeMd5Hex(file))
			.timestamp(Instant.now())
			.contentLength(file.length())
			.userId(inputFileMetadata.getUserId())
			.ipAddress(inputFileMetadata.getIpAddress())
			.fileFormat(fileFormat)
			.build();
	}
}
