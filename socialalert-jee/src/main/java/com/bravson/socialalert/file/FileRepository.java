package com.bravson.socialalert.file;

import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.bson.Document;
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
	
	public String storeFile(String filename, String contentType, long contentLength, InputStream data) {
		Document metdata = new Document("contentType", contentType).append("contentLength", contentLength);
		GridFSUploadOptions options = new GridFSUploadOptions().metadata(metdata);
		return filestore.uploadFromStream(filename, data, options).toString();
	}
	
	public FileEntity findFile(String fileId) {
		GridFSFile file = filestore.find(Filters.eq(fileId)).first();
		if (file == null) {
			return null;
		}
		return new FileEntity(file);
	}

	public void retrieveFile(FileEntity file, OutputStream os) {
		filestore.downloadToStream(new ObjectId(file.getId()), os);
	}
}
