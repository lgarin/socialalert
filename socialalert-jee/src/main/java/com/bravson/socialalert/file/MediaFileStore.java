package com.bravson.socialalert.file;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaFileProcessor;
import com.bravson.socialalert.file.media.MediaMetadata;
import com.bravson.socialalert.file.media.MediaSizeVariant;
import com.bravson.socialalert.file.picture.PictureFileProcessor;
import com.bravson.socialalert.file.store.FileStore;
import com.bravson.socialalert.file.store.TempFileFormat;
import com.bravson.socialalert.file.video.SnapshotVideoFileProcessor;
import com.bravson.socialalert.infrastructure.log.Logged;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@ManagedBean
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Logged
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
	
	public FileMetadata buildFileMetadata(File file, MediaFileFormat fileFormat, String userId, String ipAddress) throws IOException {
		String md5 = fileStore.computeMd5Hex(file);
		return FileMetadata.builder()
			.md5(md5)
			.timestamp(Instant.now())
			.contentLength(file.length())
			.userId(userId)
			.ipAddress(ipAddress)
			.fileFormat(fileFormat)
			.build();
	}
	
	public MediaMetadata buildMediaMetadata(File inputFile, MediaFileFormat fileFormat) throws Exception {
		MediaFileProcessor processor =  MediaFileFormat.VIDEO_SET.contains(fileFormat) ? videoFileProcessor : pictureFileProcessor;
		return processor.parseMetadata(inputFile);
	}
	
	public FileMetadata storeVariant(File inputFile, FileMetadata fileMetadata, MediaSizeVariant sizeVariant) throws IOException {
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
		File outputFile = fileStore.createEmptyFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), tempFormat);
		MediaFileFormat fileFormat = processor.createVariant(inputFile, outputFile, sizeVariant);
		fileStore.changeFileFormat(fileMetadata.getMd5(), fileMetadata.getTimestamp(), tempFormat, fileFormat);
		return buildFileMetadata(outputFile, fileFormat, fileMetadata.getUserId(), fileMetadata.getIpAddress());
	}

	private FileMetadata storeMedia(File inputFile, FileMetadata fileMetadata) throws IOException {
		fileStore.storeFile(inputFile, fileMetadata.getMd5(), fileMetadata.getTimestamp(), fileMetadata.getFileFormat());
		return fileMetadata;
	}
}
