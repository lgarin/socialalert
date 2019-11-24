package com.bravson.socialalert.business.user.activity;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.eclipse.microprofile.jwt.JsonWebToken;

import com.bravson.socialalert.infrastructure.layer.Repository;

@Repository
@Transactional(TxType.SUPPORTS)
public class OnlineUserRepository {

	// TODO use infinispan
	private final ConcurrentHashMap<String, Instant> onlineUserCache = new ConcurrentHashMap<>(100);
	
	private final ConcurrentHashMap<String, Set<String>> viewedMediaCache = new ConcurrentHashMap<>(100);
	
	public Instant addActiveUser(String userId) {
		if (userId == null) {
			return null;
		}
		return onlineUserCache.put(userId, Instant.now());
	}

	public boolean isUserActive(String userId) {
		return onlineUserCache.containsKey(userId);
	}
	
	public boolean addViewedMedia(String userId, String mediaUri) {
		Set<String> viewedMedia = viewedMediaCache.get(userId);
		if (viewedMedia == null) {
			viewedMedia = new HashSet<>();
			viewedMediaCache.put(userId, viewedMedia);
		}
		return viewedMedia.add(mediaUri);
	}
	
	void handleNewSession(@Observes @Initialized(SessionScoped.class) HttpSession session, JsonWebToken principal) {
		if (principal != null) {
			addActiveUser(principal.getSubject());
		}
	}

	void handleTerminatedSession(@Observes @Destroyed(SessionScoped.class) HttpSession session, JsonWebToken principal) {
		if (principal != null) {
			onlineUserCache.remove(principal.getSubject());
			viewedMediaCache.remove(principal.getSubject());
		}
	}
}
