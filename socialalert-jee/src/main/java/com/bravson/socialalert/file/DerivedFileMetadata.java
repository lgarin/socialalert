package com.bravson.socialalert.file;

import org.bson.Document;

public class DerivedFileMetadata {

	private long contentLength;
	private String contentType;
	private String sourceFilename;
	
	public DerivedFileMetadata() {
	}
	
	public DerivedFileMetadata(String sourceFilename, long contentLength, String contentType) {
		this.sourceFilename = sourceFilename;
		this.contentLength = contentLength;
		this.contentType = contentType;
	}

	public DerivedFileMetadata(Document document) {
		contentType = document.getString("contentType");
		contentLength = document.getLong("contentLength");
		sourceFilename = document.getString("sourceFilename");
	}

	public long getContentLength() {
		return contentLength;
	}

	public String getContentType() {
		return contentType;
	}

	public String getSourceFilename() {
		return sourceFilename;
	}
	
	protected void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}

	protected void setContentType(String contentType) {
		this.contentType = contentType;
	}

	protected void setSourceFilename(String sourceFilename) {
		this.sourceFilename = sourceFilename;
	}

	public Document toBson() {
		return new Document("contentType", contentType).append("contentLength", contentLength).append("sourceFilename", sourceFilename);
	}
}
