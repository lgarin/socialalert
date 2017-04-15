package com.bravson.socialalert.file;

import java.time.Instant;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Embeddable
public class FileMetadata {

	private final String md5;
	private final Instant timestamp;
	private final long contentLength;
	private final String contentType;
	private final String userId;
	private final String ipAddress;

}
