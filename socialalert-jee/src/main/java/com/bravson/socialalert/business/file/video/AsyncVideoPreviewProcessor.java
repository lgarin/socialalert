package com.bravson.socialalert.business.file.video;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.file.FileEntity;
import com.bravson.socialalert.business.file.FileMetadata;
import com.bravson.socialalert.business.file.FileRepository;
import com.bravson.socialalert.business.file.media.MediaFileFormat;
import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

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
	
	public void onAsyncEvent(@Observes AsyncVideoPreviewEvent event) {
		fileRepository.findFile(event.getFileUri()).ifPresent(this::createPreview);
	}
	
	@SneakyThrows(IOException.class)
	private void createPreview(FileEntity fileEntity) {
		FileMetadata fileMetadata = fileEntity.getFileMetadata();
		File inputFile = fileStore.getExistingFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), fileMetadata.getFileFormat());
		File previewFile = fileStore.createEmptyFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), videoFileProcessor.getPreviewFormat());
		videoFileProcessor.createPreview(inputFile, previewFile);
		fileEntity.addVariant(buildFileMetadata(previewFile, videoFileProcessor.getPreviewFormat(), fileMetadata));
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