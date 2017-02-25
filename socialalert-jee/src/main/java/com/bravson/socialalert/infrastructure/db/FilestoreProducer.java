package com.bravson.socialalert.infrastructure.db;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import com.mongodb.MongoClient;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;

@ManagedBean
@ApplicationScoped
public class FilestoreProducer {

	@Inject
	MongoClient mongoClient;
	
	@Produces
	@NamedMongoFilestore(name="*", db="*")
	@Deprecated
	public GridFSBucket getFilestore(InjectionPoint injectionPoint) {
		NamedMongoFilestore annotation = injectionPoint.getAnnotated().getAnnotation(NamedMongoFilestore.class);
		return GridFSBuckets.create(mongoClient.getDatabase(annotation.db()), annotation.name());
	}
}
