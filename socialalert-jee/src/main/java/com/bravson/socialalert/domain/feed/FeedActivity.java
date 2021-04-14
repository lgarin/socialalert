package com.bravson.socialalert.domain.feed;

import java.util.Set;

public enum FeedActivity {
	NEW_MEDIA,
	REPOST_MEDIA,
	NEW_COMMENT,
	REPOST_COMMENT,
	LIKE_MEDIA,
	DISLIKE_MEDIA,
	LIKE_COMMENT,
	DISLIKE_COMMENT,
	WATCH_MEDIA;
	
	public static final Set<FeedActivity> MEDIA_SET = Set.of(NEW_MEDIA, REPOST_MEDIA, LIKE_MEDIA, DISLIKE_MEDIA, WATCH_MEDIA);
	public static final Set<FeedActivity> COMMENT_SET = Set.of(NEW_COMMENT, REPOST_COMMENT, LIKE_COMMENT, DISLIKE_COMMENT);
}
