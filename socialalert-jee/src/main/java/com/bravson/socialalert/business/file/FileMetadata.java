package com.bravson.socialalert.business.file;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Embeddable;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

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
import lombok.With;

@Data
@Builder
@With
@AllArgsConstructor
@Embeddable
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
@Indexed
public class FileMetadata implements Serializable {

	private static final long serialVersionUID = 1L;

	@NonNull
	@KeywordField
	private String md5;
	
	@NonNull
	@GenericField
	private Instant timestamp;
	
	@NonNull
	@GenericField
	private Long contentSize;

	@NonNull
	@KeywordField
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
