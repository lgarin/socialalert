package com.bravson.socialalert.file;

import org.bson.Document;

import com.bravson.socialalert.file.picture.PictureMetadata;

public class PictureFileMetadata {

	private FileMetadata fileMetadata;
	private PictureMetadata pictureMetadata;
	
	public PictureFileMetadata(Document document) {
		pictureMetadata = new PictureMetadata(document);
		fileMetadata = new FileMetadata(document);
	}

	public PictureFileMetadata() {
	}
	
	public FileMetadata getFileMetadata() {
		return fileMetadata;
	}

	public PictureMetadata getPictureMetadata() {
		return pictureMetadata;
	}
	
	protected void setFileMetadata(FileMetadata fileMetadata) {
		this.fileMetadata = fileMetadata;
	}

	protected void setPictureMetadata(PictureMetadata pictureMetadata) {
		this.pictureMetadata = pictureMetadata;
	}

	public Document toBson() {
		Document result = fileMetadata.toBson();
		result.putAll(pictureMetadata.toBson());
		return result;
	}
	
}
