package com.bravson.socialalert.domain.user.event;

public enum UserEventType {
	BEGIN_STREAM,
	JOINED_NETWORK,
	LEFT_NETWORK,
	NEW_COMMENT,
	LIKE_MEDIA,
	DISLIKE_MEDIA,
	LIKE_COMMENT,
	DISLIKE_COMMENT,
	WATCH_MEDIA;
}
