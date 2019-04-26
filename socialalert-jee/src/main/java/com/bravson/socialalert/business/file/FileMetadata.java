package com.bravson.socialalert.business.file;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Embeddable;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.domain.media.format.MediaSizeVariant;
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
public class FileMetadata implements Serializable {

	private static final long serialVersionUID = 1L;

	@NonNull
	@Field(analyze=Analyze.NO)
	private String md5;
	
	@NonNull
	@Field
	private Instant timestamp;
	
	@NonNull
	@Field
	private Long contentSize;

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
