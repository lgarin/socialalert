package com.bravson.socialalert.business.user.authentication;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "of")
public class LoginToken {

	@NonNull
	private String accessToken;
	
	@NonNull
	private String refreshToken;
	
	@NonNull
	private Instant expiration;
}
