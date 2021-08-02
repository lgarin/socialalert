package com.bravson.socialalert.business.user.authentication;

import javax.inject.Inject;

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
public class AuthenticationHealthCheck implements HealthCheck {
	
	@Inject
	@NonNull
	AuthenticationRepository repository;
	
	@Override
	public HealthCheckResponse call() {
		return HealthCheckResponse.builder()
				.name("Keycloak connection health check")
				.status(repository.isAvailable())
				.build();
	}

}
