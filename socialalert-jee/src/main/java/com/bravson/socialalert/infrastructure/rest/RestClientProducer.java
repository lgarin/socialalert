package com.bravson.socialalert.infrastructure.rest;

import org.jboss.resteasy.plugins.providers.JaxrsFormProvider;
import org.slf4j.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;


@ApplicationScoped
public class RestClientProducer {

	@Inject
	Logger logger;
	
	private ClientBuilder httpClientBuilder;
	
	@PostConstruct
	void init() {
		httpClientBuilder = ClientBuilder.newBuilder();
		httpClientBuilder.register(JaxrsFormProvider.class);
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
