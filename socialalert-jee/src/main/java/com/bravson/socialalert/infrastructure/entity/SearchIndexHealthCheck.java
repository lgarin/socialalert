package com.bravson.socialalert.infrastructure.entity;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

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
				.state(isAvailable())
				.build();
	}
	
	private boolean isAvailable() {
		Response response = httpClient.target(healthUrl).request().get();
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return false;
		}
		return true;
	}
	
}
