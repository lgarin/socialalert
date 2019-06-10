package com.bravson.socialalert.business.user.authentication;

import java.time.Instant;

import com.bravson.socialalert.infrastructure.rest.InstantDeserializer;
import com.bravson.socialalert.infrastructure.rest.InstantSerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Data
@Builder
@Setter(AccessLevel.NONE)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticationInfo {

	@NonNull
	private String id;
	
	@NonNull
	private String username;
	
	private String email;
	
	@JsonSerialize(using=InstantSerializer.class)
	@JsonDeserialize(using=InstantDeserializer.class)
	@NonNull
	private Instant createdTimestamp;
	
	@JsonProperty("email_verified")
	private boolean emailVerified;
}
