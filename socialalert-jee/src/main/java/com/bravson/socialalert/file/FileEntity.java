package com.bravson.socialalert.file;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaMetadata;
import com.bravson.socialalert.file.media.MediaSizeVariant;
import com.bravson.socialalert.infrastructure.entity.VersionedEntity;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity(name="File")
@Indexed
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class FileEntity extends VersionedEntity {

	@ElementCollection(fetch=FetchType.EAGER)
	@IndexedEmbedded
	private List<FileMetadata> fileVariants;
		
	@Getter
	@NonNull
	@Embedded
	private MediaMetadata mediaMetadata;
	
	
	public static FileEntity of(@NonNull FileMetadata fileMetadata, @NonNull MediaMetadata mediaMetadata) {
		if (fileMetadata.getSizeVariant() != MediaSizeVariant.MEDIA) {
			throw new IllegalArgumentException("Size variant must be " + MediaSizeVariant.MEDIA.getVariantName());
		}
		FileEntity entity = new FileEntity();
		entity.versionInfo = VersionInfo.of(fileMetadata.getUserId(), fileMetadata.getIpAddress());
		entity.id = fileMetadata.buildFileUri();
		entity.mediaMetadata = mediaMetadata;
		entity.addVariant(fileMetadata);
		return entity;
	}
	
	private Optional<FileMetadata> findFileMetadata(MediaSizeVariant sizeVariant) {
		if (fileVariants == null) {
			return Optional.empty();
		}
		return fileVariants.stream().filter(v -> v.getSizeVariant() == sizeVariant).findAny();
	}
	
	public Optional<MediaFileFormat> findVariantFormat(@NonNull MediaSizeVariant sizeVariant) {
		return findFileMetadata(sizeVariant).map(FileMetadata::getFileFormat);
	}

	public boolean addVariant(@NonNull FileMetadata metadata) {
		if (findFileMetadata(metadata.getSizeVariant()).isPresent()) {
			return false;
		}
		if (fileVariants == null) {
			fileVariants = new ArrayList<>();
		}
		fileVariants.add(metadata);
		return true;
	}

	public FileMetadata getFileMetadata() {
		return findFileMetadata(MediaSizeVariant.MEDIA).orElseThrow(IllegalStateException::new);
	}

	public boolean isTemporary(MediaFileFormat format) {
		return getFileMetadata().isVideo() && format == MediaFileFormat.PREVIEW_JPG;
	}

	public boolean isVideo() {
		return getFileMetadata().isVideo();
	}
	
	public String getUserId() {
		return versionInfo.getUserId();
	}
}
