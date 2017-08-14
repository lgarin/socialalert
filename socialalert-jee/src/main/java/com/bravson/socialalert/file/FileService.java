package com.bravson.socialalert.file;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaSizeVariant;
import com.bravson.socialalert.file.store.FileStore;
import com.bravson.socialalert.infrastructure.log.Logged;

import lombok.NonNull;

@ManagedBean
@Transactional
@Logged
public class FileService {

	@Resource(name="mediaCacheMaxAge")
	int mediaCacheMaxAge;
	
	@Inject
	FileRepository mediaRepository;

	@Inject
	FileStore fileStore;

	private Optional<FileResponse> streamFile(String fileUri, MediaSizeVariant sizeVariant) throws IOException {
		FileEntity fileEntity = mediaRepository.findFile(fileUri).orElse(null);
		if (fileEntity == null) {
			return Optional.empty();
		}
        
		MediaFileFormat fileFormat = fileEntity.findVariantFormat(sizeVariant).orElse(null);
		if (fileFormat == null) {
			return Optional.empty();
		}
		FileMetadata fileMetadata = fileEntity.getMediaFileMetadata();
		File file = fileStore.getExistingFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), fileFormat);
        Integer maxAge = fileEntity.isTemporary(fileFormat) ? null : mediaCacheMaxAge;
		return Optional.of(toFileResponse(file, fileFormat, maxAge));
	}
	
	private FileResponse toFileResponse(File file, MediaFileFormat format, Integer maxAge) {
		return FileResponse.builder().file(file).contentType(format.getContentType()).etag(format.name()).maxAge(maxAge).build();
	}

	public Optional<FileResponse> download(@NonNull String fileUri) throws IOException {
		return streamFile(fileUri, MediaSizeVariant.MEDIA);
	}
	
	public Optional<FileResponse> preview(@NonNull String fileUri) throws IOException {
		return streamFile(fileUri, MediaSizeVariant.PREVIEW);
	}
	
	public Optional<FileResponse> thumbnail(@NonNull String fileUri) throws IOException {
		return streamFile(fileUri, MediaSizeVariant.THUMBNAIL);
	}
}
