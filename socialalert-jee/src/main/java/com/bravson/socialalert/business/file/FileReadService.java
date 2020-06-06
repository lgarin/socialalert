package com.bravson.socialalert.business.file;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.domain.media.format.MediaSizeVariant;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Service
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class FileReadService {

	@Inject
	FileRepository mediaRepository;

	@Inject
	FileStore fileStore;
	
	private Optional<FileResponse> createFileResponse(String fileUri, MediaSizeVariant sizeVariant) throws IOException {
		FileEntity fileEntity = mediaRepository.findFile(fileUri).orElse(null);
		if (fileEntity == null) {
			return Optional.empty();
		}
        
		MediaFileFormat fileFormat = fileEntity.findVariantFormat(sizeVariant).orElse(null);
		if (fileFormat == null) {
			return Optional.empty();
		}
		FileMetadata fileMetadata = fileEntity.getFileMetadata();
		File file = fileStore.getExistingFile(fileMetadata.getMd5(), fileMetadata.getFormattedDate(), fileFormat);
		return Optional.of(new FileResponse(file, fileFormat));
	}
	
	private Optional<FileResponse> createFileResponse(String fileUri, MediaFileFormat fileFormat) throws IOException {
		FileEntity fileEntity = mediaRepository.findFile(fileUri).orElse(null);
		if (fileEntity == null) {
			return Optional.empty();
		}
		FileMetadata fileMetadata = fileEntity.getFileMetadata();
		File file = fileStore.findExistingFile(fileMetadata.getMd5(), fileMetadata.getFormattedDate(), fileFormat).orElse(null);
		if (file == null) {
			return Optional.empty();
		}
		return Optional.of(new FileResponse(file, fileFormat));
	}

	public Optional<FileResponse> download(@NonNull String fileUri) throws IOException {
		return createFileResponse(fileUri, MediaSizeVariant.MEDIA);
	}
	
	public Optional<FileResponse> preview(@NonNull String fileUri) throws IOException {
		return createFileResponse(fileUri, MediaFileFormat.PREVIEW_JPG);
	}
	
	public Optional<FileResponse> thumbnail(@NonNull String fileUri) throws IOException {
		return createFileResponse(fileUri, MediaFileFormat.THUMBNAIL_JPG);
	}
	
	public Optional<FileResponse> stream(@NonNull String fileUri) throws IOException {
		return createFileResponse(fileUri, MediaFileFormat.PREVIEW_MP4);
	}
}
