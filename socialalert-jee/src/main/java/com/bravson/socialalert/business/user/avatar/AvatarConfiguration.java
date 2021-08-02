package com.bravson.socialalert.business.user.avatar;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.ConfigMapping.NamingStrategy;

@ConfigMapping(prefix = "avatar", namingStrategy = NamingStrategy.VERBATIM)
public interface AvatarConfiguration {

	int smallSize();
	
	int largeSize();
}
