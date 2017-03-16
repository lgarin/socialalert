package com.bravson.socialalert.file;

import org.bson.Document;

public class FileMetadata {

	private long contentLength;
	private String contentType;
	private String userId;
	private String ipAddress;
	
	public FileMetadata() {
	}
	
	public FileMetadata(Document document) {
		contentType = document.getString("contentType");
		contentLength = document.getLong("contentLength");
		userId = document.getString("userId");
		ipAddress = document.getString("ipAddress");
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
	
	public String getIpAddress() {
		return ipAddress;
	}
	
	protected void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}

	protected void setContentType(String contentType) {
		this.contentType = contentType;
	}

	protected void setUserId(String userId) {
		this.userId = userId;
	}
	
	protected void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Document toBson() {
		return new Document("contentType", contentType).append("contentLength", contentLength).append("userId", userId).append("ipAddress", ipAddress);
	}
}
