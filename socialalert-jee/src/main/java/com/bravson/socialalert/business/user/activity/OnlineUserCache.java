package com.bravson.socialalert.business.user.activity;

import java.time.Duration;
import java.time.Instant;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.bravson.socialalert.infrastructure.layer.Repository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@Repository
@Transactional(TxType.SUPPORTS)
public class OnlineUserCache {

	// TODO use infinispan
	private Cache<String, UserSession> localCache;
	
	@ConfigProperty(name = "user.sessionTimeout")
	Duration sessionTimeout;
	
	@PostConstruct
	void init() {
		localCache = Caffeine.newBuilder().expireAfterWrite(sessionTimeout).build();
	}
	
	public void addActiveUser(String userId) {
		if (userId == null) {
			return;
		}
		UserSession session = localCache.get(userId, UserSession::new);
		session.setLastAccess(Instant.now());
	}

	public boolean isUserActive(String userId) {
		return localCache.getIfPresent(userId) != null;
	}
	
	public boolean addViewedMedia(String userId, String mediaUri) {
		UserSession session = localCache.get(userId, UserSession::new);
		return session.addViewedMedia(mediaUri);
	}
	
	public void removeUser(String userId) {
		localCache.invalidate(userId);
	}
	
	public void clearAll() {
		localCache.invalidateAll();
	}
}
