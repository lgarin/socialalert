package com.bravson.socialalert;

import javax.annotation.ManagedBean;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

@ManagedBean
public class CollectionProducer {

	@Inject
	MongoClient mongoClient;
	
	@Produces
	@NamedMongoCollection(name="*", db="*")
	public MongoCollection<Document> getOrCreateCollection(InjectionPoint injectionPoint) {
		NamedMongoCollection annotation = injectionPoint.getAnnotated().getAnnotation(NamedMongoCollection.class);
		return mongoClient.getDatabase(annotation.db()).getCollection(annotation.name());
	}
}
