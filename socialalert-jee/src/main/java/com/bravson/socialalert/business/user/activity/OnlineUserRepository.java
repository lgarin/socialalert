package com.bravson.socialalert.business.user.activity;

import java.security.Principal;
import java.time.Instant;
import java.util.HashMap;

import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.bravson.socialalert.infrastructure.layer.Repository;

@Repository
@Transactional(TxType.SUPPORTS)
public class OnlineUserRepository {

	private final HashMap<String, Instant> onlineUserCache = new HashMap<String, Instant>();
	
	public void addActiveUser(String userId) {
		onlineUserCache.put(userId, Instant.now());
	}

	public boolean isUserActive(String userId) {
		return onlineUserCache.containsKey(userId);
	}
	
	void handleNewSession(@Observes @Initialized(SessionScoped.class) HttpSession session, Principal principal) {
		if (principal != null) {
			addActiveUser(principal.getName());
		}
	}

	void handleTerminatedSession(@Observes @Destroyed(SessionScoped.class) HttpSession session, Principal principal) {
		if (principal != null) {
			onlineUserCache.remove(principal.getName());
		}
	}
}
