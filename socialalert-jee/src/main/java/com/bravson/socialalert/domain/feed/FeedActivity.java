package com.bravson.socialalert.domain.feed;

import java.util.EnumSet;

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
	
	public static EnumSet<FeedActivity> MEDIA_SET = EnumSet.of(NEW_MEDIA, REPOST_MEDIA, LIKE_MEDIA, DISLIKE_MEDIA, WATCH_MEDIA);
	public static EnumSet<FeedActivity> COMMENT_SET = EnumSet.of(NEW_COMMENT, REPOST_COMMENT, LIKE_COMMENT, DISLIKE_COMMENT);
}
