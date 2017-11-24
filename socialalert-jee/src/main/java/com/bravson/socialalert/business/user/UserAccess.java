package com.bravson.socialalert.business.user;

import java.io.Serializable;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor="of")
public class UserAccess implements Serializable {

	private static final long serialVersionUID = 1L;

	@NonNull
	private String userId;
	
	@NonNull
	private String ipAddress;
}
