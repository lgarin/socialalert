package com.bravson.socialalert;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.bson.BsonDocument;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

@ManagedBean
@ApplicationScoped
public class CollectionProducer {

	@Inject
	MongoClient mongoClient;
	
	@Produces
	@NamedMongoCollection(name="*", db="*")
	public MongoCollection<BsonDocument> getOrCreateCollection(InjectionPoint injectionPoint) {
		NamedMongoCollection annotation = injectionPoint.getAnnotated().getAnnotation(NamedMongoCollection.class);
		return mongoClient.getDatabase(annotation.db()).getCollection(annotation.name(), BsonDocument.class);
	}
}
