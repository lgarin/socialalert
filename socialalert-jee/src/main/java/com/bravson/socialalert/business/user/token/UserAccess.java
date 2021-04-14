package com.bravson.socialalert.business.user.token;

import java.io.Serializable;

public interface UserAccess extends Serializable {

	public String getUserId();
	
	public String getIpAddress();
	
	public String getUsername();
	
	public String getEmail();
}
