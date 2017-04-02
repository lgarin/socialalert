package com.bravson.socialalert.file;

import org.bson.Document;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class FileMetadata {

	@Getter
	@Setter(AccessLevel.PROTECTED)
	private long contentLength;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private String contentType;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private String userId;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private String ipAddress;

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
