package com.bravson.socialalert.domain.user;

import java.time.Instant;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LoginTokenResponse {

	@NonNull
	private String accessToken;
	
	@NonNull
	private String refreshToken;
	
	@NonNull
	@Schema(description="The access token expiration timestamp in milliseconds since the epoch.", implementation=Long.class)
	private Instant expiration;
}
