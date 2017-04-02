package com.bravson.socialalert.file;

import org.bson.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FileMetadata {

	private final long contentLength;
	private final String contentType;
	private final String userId;
	private final String ipAddress;

	public FileMetadata(Document document) {
		contentType = document.getString("contentType");
		contentLength = document.getLong("contentLength");
		userId = document.getString("userId");
		ipAddress = document.getString("ipAddress");
	}

	public Document toBson() {
		return new Document("contentType", contentType).append("contentLength", contentLength).append("userId", userId).append("ipAddress", ipAddress);
	}
}
