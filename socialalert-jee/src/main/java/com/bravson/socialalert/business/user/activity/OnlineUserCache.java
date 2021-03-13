package com.bravson.socialalert.business.user.activity;

import java.time.Duration;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

import com.bravson.socialalert.infrastructure.entity.DeleteEntity;
import com.bravson.socialalert.infrastructure.layer.Repository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;

@Repository
@Transactional(TxType.SUPPORTS)
public class OnlineUserCache implements RemovalListener<String, UserSession> {

	// TODO use infinispan
	private Cache<String, UserSession> localCache;
	
	@ConfigProperty(name = "user.sessionTimeout")
	Duration sessionTimeout;
	
	@Inject
	@DeleteEntity
	Event<UserSession> deletedSessionEvent;
	
	@Inject
	Logger logger;
	
	@PostConstruct
	void init() {
		localCache = Caffeine.newBuilder().expireAfterAccess(sessionTimeout).removalListener(this).build();
	}
	
	@Override
	public void onRemoval(String key, UserSession value, RemovalCause cause) {
		logger.info("Session expired for " + key);
		deletedSessionEvent.fire(value);
	}
	
	public UserSession addActiveUser(String userId) {
		if (userId == null) {
			return null;
		}
		return localCache.get(userId, UserSession::new);
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
