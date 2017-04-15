package com.bravson.socialalert.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
class DerivedFileMetadata {
	
	private final String sourceFilename;
	private final long contentLength;
	private final String contentType;
}
