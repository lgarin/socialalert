package com.bravson.socialalert.business.file;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.file.media.MediaFileFormat;
import com.bravson.socialalert.business.file.media.MediaSizeVariant;
import com.bravson.socialalert.business.file.store.FileStore;
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
		File file = fileStore.getExistingFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), fileFormat);
		return Optional.of(new FileResponse(file, fileFormat, fileEntity.isTemporary(fileFormat)));
	}

	public Optional<FileResponse> download(@NonNull String fileUri) throws IOException {
		return createFileResponse(fileUri, MediaSizeVariant.MEDIA);
	}
	
	public Optional<FileResponse> preview(@NonNull String fileUri) throws IOException {
		return createFileResponse(fileUri, MediaSizeVariant.PREVIEW);
	}
	
	public Optional<FileResponse> thumbnail(@NonNull String fileUri) throws IOException {
		return createFileResponse(fileUri, MediaSizeVariant.THUMBNAIL);
	}
}
