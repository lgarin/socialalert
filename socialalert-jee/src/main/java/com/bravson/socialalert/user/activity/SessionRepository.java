package com.bravson.socialalert.user.activity;

import java.time.Instant;

import javax.annotation.ManagedBean;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.spi.CachingProvider;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.bravson.socialalert.infrastructure.log.Logged;
import com.bravson.socialalert.user.AuthenticationConfiguration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@ManagedBean
@ApplicationScoped
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Logged
public class SessionRepository {

	private static final String ONLINE_USER_CACHE_NAME = "onlineUserCache";
	
	private CachingProvider cachingProvider = Caching.getCachingProvider();
	private CacheManager cacheManager = cachingProvider.getCacheManager();
	private Cache<String, Instant> onlineUserCache;
	
	@Inject 
	public SessionRepository(AuthenticationConfiguration authConfig) {
		onlineUserCache = cacheManager.getCache(ONLINE_USER_CACHE_NAME, String.class, Instant.class);
		if (onlineUserCache == null) {
			MutableConfiguration<String, Instant> cacheConfig = new MutableConfiguration<>();
			cacheConfig.setTypes(String.class, Instant.class)
				.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(authConfig.getSessionDuration()));
			onlineUserCache = cacheManager.createCache(ONLINE_USER_CACHE_NAME, cacheConfig);
		}
	}

	public void addActiveUser(String userId) {
		onlineUserCache.put(userId, Instant.now());
	}

	public boolean isUserActive(String userId) {
		return onlineUserCache.containsKey(userId);
	}
}
