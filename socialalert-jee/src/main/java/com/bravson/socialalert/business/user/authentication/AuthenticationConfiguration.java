package com.bravson.socialalert.business.user.authentication;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.ConfigMapping.NamingStrategy;

@ConfigMapping(prefix = "auth", namingStrategy = NamingStrategy.VERBATIM)
public interface AuthenticationConfiguration {

	String loginUrl();
	
	String logoutUrl();
	
	String userInfoUrl();
	
	String configUrl();
	
	String adminLoginUrl();
	
	String adminClientId();
	
	String adminUsername();
	
	String adminPassword();
	
	String userCreateUrl();
	
	String userUpdateUrl();
	
	String passwordResetUrl();
	
	String loginClientId();
	
	String clientSecret();
}
