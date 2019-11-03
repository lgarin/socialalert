package com.bravson.socialalert.business.user.authentication;

import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties(prefix = "auth")
public interface AuthenticationConfiguration {

	String getLoginUrl();
	
	String getLogoutUrl();
	
	String getUserInfoUrl();
	
	String getLoginClientId();
	
	String getClientSecret();
}
