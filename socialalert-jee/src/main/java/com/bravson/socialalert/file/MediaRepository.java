package com.bravson.socialalert.file;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.bravson.socialalert.infrastructure.db.NamedMongoFilestore;
import com.mongodb.client.gridfs.GridFSBucket;

@ManagedBean
public class MediaRepository extends FileRepository {
	
	@Inject 
	public MediaRepository(@NamedMongoFilestore(db="socialalert", name="media") GridFSBucket mediaStore) {
		super(mediaStore);
	}
}
