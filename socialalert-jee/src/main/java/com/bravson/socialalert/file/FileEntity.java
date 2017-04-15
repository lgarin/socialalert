package com.bravson.socialalert.file;

import java.util.Optional;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.bravson.socialalert.file.media.MediaFileConstants;
import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaMetadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity(name="MediaFile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileEntity {

	@Getter
	@NonNull
	@Id
	private String fileUri;
	
	@Getter
	@ElementCollection
	private Set<MediaFileFormat> fileFormats;
		
	@Getter
	@Embedded
	private FileMetadata fileMetadata;
	
	@Getter
	@Embedded
	private MediaMetadata mediaMetadata;
	
	public Optional<MediaFileFormat> findVariantFormat(String variantName) {
		if (fileFormats == null) {
			return Optional.empty();
		}
		return fileFormats.stream().filter(f -> f.getSizeVariant().equals(MediaFileConstants.MEDIA_VARIANT)).findAny();
	}
}
