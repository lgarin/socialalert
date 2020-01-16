package com.bravson.socialalert.business.user.authentication;

import io.reactivex.annotations.NonNull;
import lombok.Builder;
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
