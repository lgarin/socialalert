package com.bravson.socialalert.file;

import java.time.Instant;

import com.mongodb.client.gridfs.model.GridFSFile;

public class FileEntity {

	private final String id;
	private final String filename;
	private final long length;
	private final Instant uploadTimestamp;
	private final String md5;
	private final FileMetadata metadata;

	public FileEntity(GridFSFile file) {
		id = file.getId().toString();
		metadata = new FileMetadata(file.getMetadata());
		length = file.getLength();
		filename = file.getFilename();
		md5 = file.getMD5();
		uploadTimestamp = file.getUploadDate().toInstant();
	}
	public String getId() {
		return id;
	}
	public String getFilename() {
		return filename;
	}
	public long getLength() {
		return length;
	}
	public String getContentType() {
		return metadata.getContentType();
	}
	public Instant getUploadTimestamp() {
		return uploadTimestamp;
	}
	public String getMd5() {
		return md5;
	}
	public String getUserId() {
		return metadata.getUserId();
	}
}
