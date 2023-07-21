package com.bravson.socialalert.business.user.session;

import java.time.Duration;
import java.util.Optional;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import jakarta.annotation.PostConstruct;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

import com.bravson.socialalert.infrastructure.entity.DeleteEntity;
import com.bravson.socialalert.infrastructure.entity.NewEntity;
import com.bravson.socialalert.infrastructure.layer.Repository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.github.benmanes.caffeine.cache.Scheduler;

@Repository
@Transactional(TxType.SUPPORTS)
public class UserSessionCache implements RemovalListener<String, UserSession> {

	// TODO use infinispan
	private Cache<String, UserSession> localCache;
	
	@ConfigProperty(name = "user.sessionTimeout")
	Duration sessionTimeout;
	
	@Inject
	@DeleteEntity
	Event<UserSession> deletedSessionEvent;
	
	@Inject
	@NewEntity
	Event<UserSession> createdSessionEvent;
	
	@Inject
	Logger logger;
	
	@PostConstruct
	void init() {
		localCache = Caffeine.newBuilder()
				.expireAfterAccess(sessionTimeout)
				.removalListener(this)
				.scheduler(Scheduler.systemScheduler())
				.build();
	}
	
	@Override
	public void onRemoval(String key, UserSession value, RemovalCause cause) {
		logger.info("Session expired for {}", key);
		deletedSessionEvent.fire(value);
	}
	
	public Optional<UserSession> findActiveUser(String userId) {
		return Optional.ofNullable(localCache.getIfPresent(userId));
	}
	
	public UserSession addActiveUser(String userId) {
		if (userId == null) {
			return null;
		}
		return localCache.get(userId, this::createNewSession);
	}
	
	private UserSession createNewSession(String userId) {
		UserSession session = new UserSession(userId);
		createdSessionEvent.fire(session);
		return session;
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
