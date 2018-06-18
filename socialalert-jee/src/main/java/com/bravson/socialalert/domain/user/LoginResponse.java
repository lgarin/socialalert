package com.bravson.socialalert.domain.user;

import java.time.Instant;

import com.bravson.socialalert.infrastructure.rest.InstantDeserializer;
import com.bravson.socialalert.infrastructure.rest.InstantSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class LoginResponse {

	@NonNull
	private final String accessToken;
	
	@NonNull
	private final String username;
	
	private final String email;
	
	private final String country;
	
	private final String language;
	
	private final String imageUri;

	@Schema(description="The user creation timestamp in milliseconds since the epoch.")
	@JsonSerialize(using=InstantSerializer.class)
	@JsonDeserialize(using=InstantDeserializer.class)
	@NonNull
	private Instant creation;
}
