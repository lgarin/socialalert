package com.bravson.socialalert.infrastructure.rest;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.slf4j.Logger;

import io.quarkus.resteasy.common.runtime.jsonb.QuarkusJsonbContextResolver;


@ApplicationScoped
public class RestClientProducer {

	@Inject
	Logger logger;
	
	@Inject
	QuarkusJsonbContextResolver jsonbContextResolver;
	
	private ClientBuilder httpClientBuilder;
	
	@PostConstruct
	void init() {
		httpClientBuilder = ClientBuilder.newBuilder();
		httpClientBuilder.register(jsonbContextResolver);
	}
	
	@Produces
	@RequestScoped
	public Client httpClient() {
		logger.info("Creating Http Client");
		return httpClientBuilder.build();
	}

	public void closeHttpClient(@Disposes Client httpClient) {
		logger.info("Closing Http Client");
		httpClient.close();
	}
}
