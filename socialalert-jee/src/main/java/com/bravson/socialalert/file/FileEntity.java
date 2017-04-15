package com.bravson.socialalert.file;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.bravson.socialalert.file.media.MediaFileConstants;
import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaMetadata;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity(name="MediaFile")
@RequiredArgsConstructor
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
	private Set<MediaFileFormat> fileFormats;
		
	@Getter
	@NonNull
	@Embedded
	private FileMetadata fileMetadata;
	
	@Getter
	@NonNull
	@Embedded
	private MediaMetadata mediaMetadata;
	
	public Optional<MediaFileFormat> findVariantFormat(String variantName) {
		if (fileFormats == null) {
			return Optional.empty();
		}
		return fileFormats.stream().filter(f -> f.getSizeVariant().equals(MediaFileConstants.MEDIA_VARIANT)).findAny();
	}

	public boolean addMediaFormat(MediaFileFormat format) {
		if (fileFormats == null) {
			fileFormats = new HashSet<MediaFileFormat>();
		}
		return fileFormats.add(format);
	}
	
	public String getMd5() {
		return fileMetadata.getMd5();
	}
	
	public Instant getTimestamp() {
		return fileMetadata.getTimestamp();
	}

	public String getContentType() {
		return fileMetadata.getContentType();
	}
}
