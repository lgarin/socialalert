package com.bravson.socialalert.file;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.bravson.socialalert.infrastructure.db.NamedMongoFilestore;
import com.mongodb.client.gridfs.GridFSBucket;

@ManagedBean
public class ThumbnailRepository extends FileRepository {
	
	private static final int CHUNK_SIZE = 16000;
	
	@Inject 
	public ThumbnailRepository(@NamedMongoFilestore(db="socialalert", name="thumbnail") GridFSBucket mediaStore) {
		super(mediaStore, CHUNK_SIZE);
	}
}
