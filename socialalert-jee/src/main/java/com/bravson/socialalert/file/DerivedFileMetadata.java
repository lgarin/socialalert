package com.bravson.socialalert.file;

import org.bson.Document;

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
	
	public DerivedFileMetadata(Document document) {
		contentType = document.getString("contentType");
		contentLength = document.getLong("contentLength");
		sourceFilename = document.getString("sourceFilename");
	}
	
	public Document toBson() {
		return new Document("contentType", contentType).append("contentLength", contentLength).append("sourceFilename", sourceFilename);
	}
}
