package com.bravson.socialalert.business.user.authentication;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class CredentialRepresentation {

	private boolean temporary;
	
	@NonNull
	private String type;
	
	@NonNull
	private String value;
}
