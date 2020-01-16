package com.bravson.socialalert.business.user.authentication;

import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties(prefix = "auth")
public interface AuthenticationConfiguration {

	String getLoginUrl();
	
	String getLogoutUrl();
	
	String getUserInfoUrl();
	
	String getConfigUrl();
	
	String getAdminLoginUrl();
	
	String getAdminClientId();
	
	String getAdminUsername();
	
	String getAdminPassword();
	
	String getUserCreateUrl();
	
	String getLoginClientId();
	
	String getClientSecret();
}
