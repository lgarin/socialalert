package com.bravson.socialalert.business.user.token;

import java.io.Serializable;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder
@Value
public class UserAccessToken implements UserAccess, Serializable {

	private static final long serialVersionUID = 1L;

	@NonNull
	private String userId;
	
	@NonNull
	private String ipAddress;
	
	@NonNull
	private String username;
	
	@NonNull
	private String email;
	
}
