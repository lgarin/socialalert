package com.bravson.socialalert.user.session;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.ManagedBean;
import javax.enterprise.context.SessionScoped;

import com.bravson.socialalert.infrastructure.log.Logged;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@ManagedBean
@SessionScoped
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Logged
public class UserSessionService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Set<String> viewedMedia = new HashSet<>();
	
	public boolean addViewedMedia(String mediaUri) {
		return viewedMedia.add(mediaUri);
	}
}
