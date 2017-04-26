package com.bravson.socialalert.file;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaMetadata;
import com.bravson.socialalert.file.media.MediaSizeVariant;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Entity(name="MediaFile")
@ToString(of="fileUri")
@EqualsAndHashCode(of="fileUri")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Indexed
public class FileEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@NonNull
	@Id
	private String fileUri;
	
	@ElementCollection(fetch=FetchType.EAGER)
	@IndexedEmbedded
	private List<FileMetadata> fileVariants;
		
	@Getter
	@NonNull
	@Embedded
	private MediaMetadata mediaMetadata;
	
	public static FileEntity of(FileMetadata fileMetadata, MediaMetadata mediaMetadata) {
		if (fileMetadata.getSizeVariant() != MediaSizeVariant.MEDIA) {
			throw new IllegalArgumentException("Size variant must be " + MediaSizeVariant.MEDIA.getVariantName());
		}
		FileEntity entity = new FileEntity();
		entity.fileUri = fileMetadata.buildFileUri();
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
	
	public Optional<MediaFileFormat> findVariantFormat(MediaSizeVariant sizeVariant) {
		return findFileMetadata(sizeVariant).map(FileMetadata::getFileFormat);
	}

	public boolean addVariant(FileMetadata metadata) {
		if (findFileMetadata(metadata.getSizeVariant()).isPresent()) {
			return false;
		}
		if (fileVariants == null) {
			fileVariants = new ArrayList<>();
		}
		fileVariants.add(metadata);
		return true;
	}
	
	public FileMetadata getMediaFileMetadata() {
		return findFileMetadata(MediaSizeVariant.MEDIA).orElseThrow(IllegalStateException::new);
	}
}
