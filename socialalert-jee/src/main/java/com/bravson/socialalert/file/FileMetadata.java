package com.bravson.socialalert.file;

import java.time.Instant;

import javax.persistence.Embeddable;

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
public class FileMetadata {

	@NonNull
	private String md5;
	@NonNull
	private Instant timestamp;
	private long contentLength;
	@NonNull
	private String contentType;
	@NonNull
	private String userId;
	@NonNull
	private String ipAddress;

	public String buildFileUri() {
		return DateUtil.COMPACT_DATE_FORMATTER.format(timestamp) + "/" + md5;
	}
}
