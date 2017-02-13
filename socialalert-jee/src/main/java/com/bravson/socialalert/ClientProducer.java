package com.bravson.socialalert;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@ManagedBean
public class ClientProducer {

	@Resource(name="mongoDbUrl")
	String mongoDbUrl;

	@Produces
	public MongoClient mongoClient() {
		return new MongoClient(new MongoClientURI(mongoDbUrl));
	}
	
	@Produces
	public Client httpClient() {
		return ClientBuilder.newClient();
	}
}
