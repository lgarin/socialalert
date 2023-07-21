package com.bravson.socialalert.infrastructure.entity;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Readiness
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class SearchIndexHealthCheck implements HealthCheck {

	@ConfigProperty(name = "search.healthUrl")
	String healthUrl;
	
	@Inject
	@NonNull
	Client httpClient;
	
	@Override
	public HealthCheckResponse call() {
		return HealthCheckResponse.builder()
				.name("Elasticsearch connection health check")
				.status(isAvailable())
				.build();
	}
	
	private boolean isAvailable() {
		Response response = httpClient.target(healthUrl).request().get();
		return response.getStatus() == Status.OK.getStatusCode();
	}
	
}
