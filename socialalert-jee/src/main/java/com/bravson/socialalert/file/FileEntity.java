package com.bravson.socialalert.file;

import java.time.Instant;

import org.bson.Document;

import com.bravson.socialalert.file.picture.PictureMetadata;
import com.bravson.socialalert.file.video.VideoMetadata;
import com.mongodb.client.gridfs.model.GridFSFile;

import lombok.Getter;
import lombok.Value;

@Value
public class FileEntity {

	@Getter
	private final String id;
	
	@Getter
	private final String filename;
	
	@Getter
	private final long length;
	
	@Getter
	private final Instant uploadTimestamp;
	
	@Getter
	private final String md5;
	
	private final Document metadata;
	
	@Getter(lazy=true)
	private final FileMetadata fileMetadata = buildFileMetadata(metadata);
	
	@Getter(lazy=true)
	private final PictureMetadata pictureMetadata = buildPictureMetadata(metadata);
	
	@Getter(lazy=true)
	private final VideoMetadata videoMetadata = buildVideoMetadata(metadata);

	public FileEntity(GridFSFile file) {
		id = file.getId().toString();
		metadata = file.getMetadata();
		length = file.getLength();
		filename = file.getFilename();
		md5 = file.getMD5();
		uploadTimestamp = file.getUploadDate().toInstant();
	}

	private static FileMetadata buildFileMetadata(Document metadata) {
		return new FileMetadata(metadata);
	}
	
	private static PictureMetadata buildPictureMetadata(Document metadata) {
		return new PictureMetadata(metadata);
	}
	
	private static VideoMetadata buildVideoMetadata(Document metadata) {
		return new VideoMetadata(metadata);
	}
}
