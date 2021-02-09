package com.bravson.socialalert.business.user.authentication;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationInfo {

	@NonNull
	private String id;
	
	@NonNull
	private String username;
	
	private String email;
	
	@NonNull
	private Instant createdTimestamp;
	
	@JsonProperty("email_verified")
	private boolean emailVerified;
	
	@JsonProperty("firstName")
	private String firstname;
	
	@JsonProperty("lastName")
	private String lastname;
}
