package com.bravson.socialalert.file;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.bson.types.ObjectId;

import com.bravson.socialalert.infrastructure.db.NamedMongoFilestore;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;

@ManagedBean
public class FileRepository {

	@Inject @NamedMongoFilestore(db="socialalert", name="media")
	GridFSBucket filestore;
	
	public String storeFile(String filename, FileMetadata metadata, InputStream data) {
		GridFSUploadOptions options = new GridFSUploadOptions().metadata(metadata.toBson());
		return filestore.uploadFromStream(filename, data, options).toString();
	}
	
	public Optional<FileEntity> findFile(String fileId) {
		GridFSFile file = filestore.find(Filters.eq(new ObjectId(fileId))).first();
		if (file == null) {
			return Optional.empty();
		}
		return Optional.of(new FileEntity(file));
	}

	public void retrieveFile(String fileId, OutputStream os) {
		filestore.downloadToStream(new ObjectId(fileId), os);
	}
	
	public InputStream openFile(String fileId) {
		return filestore.openDownloadStream(new ObjectId(fileId));
	}
}
