package com.bravson.socialalert.infrastructure.db;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.bson.BsonDocument;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

import lombok.val;

@ManagedBean
@ApplicationScoped
public class DatastoreProducer {

	@Inject
	MongoClient mongoClient;
	
	@Inject
	Morphia mongoMapper;
	
	@Produces
	@NamedMongoCollection(name="*", db="*")
	@Deprecated
	public MongoCollection<BsonDocument> getOrCreateCollection(InjectionPoint injectionPoint) {
		NamedMongoCollection annotation = injectionPoint.getAnnotated().getAnnotation(NamedMongoCollection.class);
		return mongoClient.getDatabase(annotation.db()).getCollection(annotation.name(), BsonDocument.class);
	}
	
	@Produces
	@NamedMongoDatastore(db="*")
	public Datastore createDataStore(InjectionPoint injectionPoint) {
		val annotation = injectionPoint.getAnnotated().getAnnotation(NamedMongoDatastore.class);
		val datastore = mongoMapper.createDatastore(mongoClient, annotation.db());
		datastore.ensureIndexes();
		return datastore;
	}
}
