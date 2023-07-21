package com.bravson.socialalert.business.file;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import com.bravson.socialalert.business.file.entity.FileMetadata;
import com.bravson.socialalert.business.file.media.MediaFileProcessor;
import com.bravson.socialalert.business.file.picture.PictureFileProcessor;
import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.business.file.store.TempFileFormat;
import com.bravson.socialalert.business.file.video.SnapshotVideoFileProcessor;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.domain.media.format.MediaSizeVariant;
import com.bravson.socialalert.infrastructure.layer.Service;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Service
@Transactional(TxType.SUPPORTS)
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class MediaFileStore {

	@Inject
	@NonNull
	PictureFileProcessor pictureFileProcessor;
	
	@Inject
	@NonNull
	SnapshotVideoFileProcessor videoFileProcessor;
	
	@Inject
	@NonNull
	FileStore fileStore;
	
	public FileMetadata buildFileMetadata(@NonNull File file, @NonNull MediaFileFormat fileFormat) throws IOException {
		String md5 = fileStore.computeMd5Hex(file);
		return FileMetadata.builder()
			.md5(md5)
			.timestamp(Instant.now())
			.contentSize(file.length())
			.fileFormat(fileFormat)
			.build();
	}
	
	public FileMetadata storeVariant(@NonNull File inputFile, @NonNull FileMetadata fileMetadata, @NonNull MediaSizeVariant sizeVariant) throws IOException {
		switch (sizeVariant) {
		case MEDIA:
			return storeMedia(inputFile, fileMetadata);
		case PREVIEW:
		case THUMBNAIL:
			return storedDerivedMedia(inputFile, fileMetadata, sizeVariant);
		default:
			throw new IllegalArgumentException();
		}
	}

	private FileMetadata storedDerivedMedia(File inputFile, FileMetadata fileMetadata, MediaSizeVariant sizeVariant) throws IOException {
		MediaFileProcessor processor = fileMetadata.isVideo() ? videoFileProcessor : pictureFileProcessor;
		TempFileFormat tempFormat = new TempFileFormat(processor.getFormat(sizeVariant));
		File tempFile = fileStore.createEmptyFile(fileMetadata.getMd5(), fileMetadata.getFormattedDate(), tempFormat);
		MediaFileFormat fileFormat = processor.createVariant(inputFile, tempFile, sizeVariant);
		File outputFile = fileStore.changeFileFormat(fileMetadata.getMd5(), fileMetadata.getFormattedDate(), tempFormat, fileFormat);
		return buildFileMetadata(outputFile, fileFormat);
	}

	private FileMetadata storeMedia(File inputFile, FileMetadata fileMetadata) throws IOException {
		fileStore.storeFile(inputFile, fileMetadata.getMd5(), fileMetadata.getFormattedDate(), fileMetadata.getFileFormat());
		return fileMetadata;
	}
}
