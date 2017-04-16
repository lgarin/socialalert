package com.bravson.socialalert.file;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaMetadata;
import com.bravson.socialalert.file.media.MediaSizeVariant;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.val;

@Entity(name="MediaFile")
@ToString(of="fileUri")
@EqualsAndHashCode(of="fileUri")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class FileEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@NonNull
	@Id
	private String fileUri;
	
	@ElementCollection
	private Map<MediaSizeVariant, FileMetadata> fileVariants;
		
	@Getter
	@NonNull
	@Embedded
	private MediaMetadata mediaMetadata;
	
	public static FileEntity of(FileMetadata fileMetadata, MediaMetadata mediaMetadata) {
		if (fileMetadata.getSizeVariant() != MediaSizeVariant.MEDIA) {
			throw new IllegalArgumentException("Size variant must be " + MediaSizeVariant.MEDIA.getVariantName());
		}
		val entity = new FileEntity();
		entity.fileUri = fileMetadata.buildFileUri();
		entity.mediaMetadata = mediaMetadata;
		entity.addVariant(fileMetadata);
		return entity;
	}
	
	public Optional<MediaFileFormat> findVariantFormat(MediaSizeVariant sizeVariant) {
		if (fileVariants == null) {
			return Optional.empty();
		}
		val metadata = fileVariants.get(sizeVariant);
		if (metadata == null) {
			return Optional.empty();
		}
		return Optional.of(metadata.getFileFormat());
	}

	public boolean addVariant(FileMetadata metadata) {
		if (fileVariants == null) {
			fileVariants = new EnumMap<>(MediaSizeVariant.class);
		}
		val sizeVariant = metadata.getSizeVariant();
		if (fileVariants.containsKey(sizeVariant)) {
			return false;
		}
		fileVariants.put(sizeVariant, metadata);
		return true;
	}
	
	public FileMetadata getMediaFileMetadata() {
		if (fileVariants == null || !fileVariants.containsKey(MediaSizeVariant.MEDIA)) {
			throw new IllegalStateException("No media variant defined for " + this);
		}
		return fileVariants.get(MediaSizeVariant.MEDIA);
	}
}
