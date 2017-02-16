package com.bravson.socialalert;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.slf4j.Logger;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@ManagedBean
@ApplicationScoped
public class ClientProducer {

	@Inject
	Logger logger;
	
	@Resource(name="mongoDbUrl")
	String mongoDbUrl;

	private ClientBuilder httpClientBuilder = ClientBuilder.newBuilder();
	
	@Produces
	@ApplicationScoped
	public MongoClient mongoClient() {
		logger.info("Creating Mongo Client");
		return new MongoClient(new MongoClientURI(mongoDbUrl));
	}
	
	@Produces
	public Client httpClient() {
		logger.info("Creating Http Client");
		return httpClientBuilder.build();
	}
	
	public void closeMongoClient(@Disposes MongoClient mongoClient) {
		logger.info("Closing Mongo Client");
		mongoClient.close();
	}
	
	public void closeHttpClient(@Disposes Client httpClient) {
		logger.info("Closing Http Client");
		httpClient.close();
	}
}
