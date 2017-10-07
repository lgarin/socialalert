package com.bravson.socialalert.user.activity;

import java.time.Instant;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.spi.CachingProvider;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.bravson.socialalert.infrastructure.layer.Repository;
import com.bravson.socialalert.user.AuthenticationConfiguration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Repository
@Transactional(TxType.SUPPORTS)
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class OnlineUserRepository {

	private static final String ONLINE_USER_CACHE_NAME = "onlineUserCache";
	
	private CachingProvider cachingProvider = Caching.getCachingProvider();
	private CacheManager cacheManager = cachingProvider.getCacheManager();
	private Cache<String, Instant> onlineUserCache;
	
	@Inject 
	public OnlineUserRepository(AuthenticationConfiguration authConfig) {
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
