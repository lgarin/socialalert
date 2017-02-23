package com.bravson.socialalert.infrastructure.db;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@ManagedBean
@ApplicationScoped
public class DatabaseClientProducer {
	@Inject
	Logger logger;
	
	@Resource(name="mongoDbUrl")
	String mongoDbUrl;

	@Produces
	@ApplicationScoped
	public MongoClient mongoClient() {
		logger.info("Creating Mongo Client");
		return new MongoClient(new MongoClientURI(mongoDbUrl));
	}

	public void closeMongoClient(@Disposes MongoClient mongoClient) {
		logger.info("Closing Mongo Client");
		mongoClient.close();
	}
}
