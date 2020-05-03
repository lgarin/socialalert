package com.bravson.socialalert.business.user.authentication;

import java.time.Instant;

import javax.json.bind.annotation.JsonbProperty;

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
	
	@JsonbProperty("email_verified")
	private boolean emailVerified;
	
	@JsonbProperty("firstName")
	private String firstname;
	
	@JsonbProperty("lastName")
	private String lastname;
}
