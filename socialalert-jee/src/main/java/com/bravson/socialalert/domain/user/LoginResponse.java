package com.bravson.socialalert.domain.user;

import java.time.Instant;

import com.bravson.socialalert.infrastructure.rest.InstantDeserializer;
import com.bravson.socialalert.infrastructure.rest.InstantSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

	@NonNull
	private String accessToken;
	
	@NonNull
	private String username;
	
	private String email;
	
	private String country;
	
	private String language;
	
	private String imageUri;

	@Schema(description="The user creation timestamp in milliseconds since the epoch.")
	@JsonSerialize(using=InstantSerializer.class)
	@JsonDeserialize(using=InstantDeserializer.class)
	@NonNull
	private Instant creation;
}
