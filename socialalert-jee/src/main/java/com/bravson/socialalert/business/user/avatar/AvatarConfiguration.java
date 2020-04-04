package com.bravson.socialalert.business.user.avatar;

import io.quarkus.arc.config.ConfigProperties;
import io.quarkus.arc.config.ConfigProperties.NamingStrategy;

@ConfigProperties(prefix = "avatar", namingStrategy = NamingStrategy.VERBATIM)
public interface AvatarConfiguration {

	int getSmallSize();
	
	int getLargeSize();
}
