package com.bravson.socialalert.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import org.bson.BsonObjectId;
import org.bson.Document;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;

import lombok.val;

public abstract class FileRepository {

	private static final int CHUNK_SIZE = 65536;
	
	private GridFSBucket fileStore;
	
	public FileRepository(GridFSBucket fileStore) {
		this.fileStore = fileStore;
	}

	public String storeMedia(MediaFileMetadata metadata, File file) throws IOException {
		val id = new BsonObjectId();
		val filename = id.getValue().toHexString();
		return uploadFile(filename, id, file, metadata.toBson());
	}
	
	public String storeDerived(String sourceFilename, String contentType, File file) throws IOException {
		val metadata = new DerivedFileMetadata(sourceFilename, file.length(), contentType);
		val id = new BsonObjectId();
		val filename = metadata.getSourceFilename();
		return uploadFile(filename, id, file, metadata.toBson());
	}
	
	private String uploadFile(String filename, BsonObjectId id, File file, Document metadata) throws IOException {
		val options = new GridFSUploadOptions().chunkSizeBytes(CHUNK_SIZE).metadata(metadata);
		try (val is = new FileInputStream(file)) {
			fileStore.uploadFromStream(id, filename, is, options);
			return filename;
		}
	}
	
	public Optional<FileEntity> findFile(String fileId) {
		val file = fileStore.find(Filters.eq("filename", fileId)).first();
		if (file == null) {
			return Optional.empty();
		}
		return Optional.of(new FileEntity(file));
	}

	public void retrieveFile(String fileId, OutputStream os) {
		fileStore.downloadToStream(fileId, os);
	}
	
	public InputStream openFile(String fileId) {
		return fileStore.openDownloadStream(fileId);
	}
}
