package com.bravson.socialalert.business.user;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor="of")
public class UserAccess {

	@NonNull
	private String userId;
	
	@NonNull
	private String ipAddress;
}
