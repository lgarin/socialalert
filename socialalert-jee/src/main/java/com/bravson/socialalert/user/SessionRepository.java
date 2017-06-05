package com.bravson.socialalert.user;

import java.time.Instant;

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
public class SessionRepository {

	private Cache<String, Instant> onlineUserCache;
	
	@Inject 
	public SessionRepository(CacheManager cacheManager, AuthenticationConfiguration authConfig) {
		MutableConfiguration<String, Instant> cacheConfig = new MutableConfiguration<>();
		cacheConfig.setTypes(String.class, Instant.class)
			.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(authConfig.getSessionDuration()));
		onlineUserCache = cacheManager.createCache("onlineUserCache", cacheConfig);
	}

	public void addActiveUser(String userId) {
		onlineUserCache.put(userId, Instant.now());
	}

	public boolean isUserActive(String userId) {
		return onlineUserCache.containsKey(userId);
	}
}
