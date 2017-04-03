package com.bravson.socialalert.file;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.bravson.socialalert.infrastructure.db.NamedMongoFilestore;
import com.mongodb.client.gridfs.GridFSBucket;

@ManagedBean
public class MediaRepository extends FileRepository {
	
	private static final int CHUNK_SIZE = 64000;
	
	@Inject 
	public MediaRepository(@NamedMongoFilestore(db="socialalert", name="media") GridFSBucket mediaStore) {
		super(mediaStore, CHUNK_SIZE);
	}
}
