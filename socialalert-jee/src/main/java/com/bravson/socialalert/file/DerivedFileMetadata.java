package com.bravson.socialalert.file;

import org.bson.Document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
class DerivedFileMetadata {
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private String sourceFilename;

	@Getter
	@Setter(AccessLevel.PROTECTED)
	private long contentLength;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private String contentType;
	
	public DerivedFileMetadata(Document document) {
		contentType = document.getString("contentType");
		contentLength = document.getLong("contentLength");
		sourceFilename = document.getString("sourceFilename");
	}
	
	public Document toBson() {
		return new Document("contentType", contentType).append("contentLength", contentLength).append("sourceFilename", sourceFilename);
	}
}
