package com.bravson.socialalert.business.user.authentication;

import java.util.List;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class UserRepresentation {
	
	@NonNull
	private String username;
	
	@NonNull
	private String email;
	
	private String firstName;
	
	private String lastName;
	
	@NonNull
	@Singular
	private List<CredentialRepresentation> credentials;
}
