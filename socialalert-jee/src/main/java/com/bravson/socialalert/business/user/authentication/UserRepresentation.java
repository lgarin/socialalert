package com.bravson.socialalert.business.user.authentication;

import java.util.List;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class UserRepresentation {
	
	private String username;
	
	private String email;
	
	private String firstName;
	
	private String lastName;
	
	@Singular
	private List<CredentialRepresentation> credentials;
}
