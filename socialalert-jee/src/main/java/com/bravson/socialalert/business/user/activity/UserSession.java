package com.bravson.socialalert.business.user.activity;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Data
class UserSession {

	@NonNull
	private final String userId;

	private Instant lastAccess;
	
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Set<String> viewedMedia;
	

	public boolean hasViewedMedia(String mediaUri) {
		return viewedMedia != null && viewedMedia.contains(mediaUri);
	}
	
	public boolean addViewedMedia(String mediaUri) {
		if (viewedMedia == null) {
			viewedMedia = new HashSet<>();
		}
		return viewedMedia.add(mediaUri);
	}
}
