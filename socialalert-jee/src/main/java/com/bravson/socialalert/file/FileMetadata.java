package com.bravson.socialalert.file;

import org.bson.Document;

public class FileMetadata {

	private final long contentLength;
	private final String contentType;
	private final String userId;
	
	public FileMetadata(String contentType, long contentLength, String userId) {
		this.contentType = contentType;
		this.contentLength = contentLength;
		this.userId = userId;
	}
	
	public FileMetadata(Document document) {
		contentType = document.getString("contentType");
		contentLength = document.getLong("contentLength");
		userId = document.getString("userId");
	}

	public long getContentLength() {
		return contentLength;
	}

	public String getContentType() {
		return contentType;
	}

	public String getUserId() {
		return userId;
	}
	
	public Document toBson() {
		return new Document("contentType", contentType).append("contentLength", contentLength).append("userId", userId);
	}
}
