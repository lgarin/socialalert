package com.bravson.socialalert.file;

import java.time.Instant;

import javax.persistence.Convert;
import javax.persistence.Embeddable;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaSizeVariant;
import com.bravson.socialalert.infrastructure.entity.InstantAttributeConverter;
import com.bravson.socialalert.infrastructure.util.DateUtil;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@Embeddable
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
@Indexed
public class FileMetadata {

	@NonNull
	@Field(analyze=Analyze.NO)
	private String md5;
	
	@NonNull
	@Convert(converter=InstantAttributeConverter.class)
	@Field
	private Instant timestamp;
	
	@NonNull
	@Field
	private Long contentLength;

	@NonNull
	@Field
	private MediaFileFormat fileFormat;

	public String buildFileUri() {
		return DateUtil.COMPACT_DATE_FORMATTER.format(timestamp) + "/" + md5;
	}

	public MediaSizeVariant getSizeVariant() {
		return fileFormat.getMediaSizeVariant();
	}
	
	public String getContentType() {
		return fileFormat.getContentType();
	}
	
	public boolean isVideo() {
		return MediaFileFormat.VIDEO_SET.contains(fileFormat);
	}
	
	public boolean isPicture() {
		return MediaFileFormat.PICTURE_SET.contains(fileFormat);
	}
}
