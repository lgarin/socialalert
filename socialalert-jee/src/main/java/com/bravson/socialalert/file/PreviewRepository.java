package com.bravson.socialalert.file;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.bravson.socialalert.infrastructure.db.NamedMongoFilestore;
import com.mongodb.client.gridfs.GridFSBucket;

@ManagedBean
public class PreviewRepository extends FileRepository {
	
	@Inject 
	public PreviewRepository(@NamedMongoFilestore(db="socialalert", name="preview") GridFSBucket mediaStore) {
		super(mediaStore);
	}
}
