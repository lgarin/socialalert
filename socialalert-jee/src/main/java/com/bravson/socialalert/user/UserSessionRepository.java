package com.bravson.socialalert.user;

import javax.annotation.ManagedBean;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.bravson.socialalert.infrastructure.log.Logged;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@ManagedBean
@ApplicationScoped
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Logged
public class UserSessionRepository {

	private Cache<String, UserInfo> onlineUserCache;
	
	@Inject 
	public UserSessionRepository(CacheManager cacheManager, AuthenticationConfiguration authConfig) {
		MutableConfiguration<String, UserInfo> cacheConfig = new MutableConfiguration<>();
		cacheConfig.setTypes(String.class, UserInfo.class)
			.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(authConfig.getSessionDuration()));
		onlineUserCache = cacheManager.createCache("onlineUserCache", cacheConfig);
	}
	

}
