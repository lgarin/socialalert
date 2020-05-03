package com.bravson.socialalert.business.user.authentication;

import io.quarkus.arc.config.ConfigProperties;
import io.quarkus.arc.config.ConfigProperties.NamingStrategy;

@ConfigProperties(prefix = "auth", namingStrategy = NamingStrategy.VERBATIM)
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
	
	String getUserUpdateUrl();
	
	String getLoginClientId();
	
	String getClientSecret();
}
