package com.bravson.socialalert.domain.feed;

import java.time.Instant;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.bravson.socialalert.domain.media.MediaInfo;
import com.bravson.socialalert.domain.media.UserContent;
import com.bravson.socialalert.domain.media.comment.MediaCommentInfo;
import com.bravson.socialalert.domain.user.UserInfo;

import lombok.Data;

@Schema(description="The information for a feed item.")
@Data
public class FeedItemInfo implements UserContent {

	@Schema(description = "The activity timestamp in milliseconds since the epoch.", implementation = Long.class)
	private Instant creation;
	
	private String creatorId;
	
	private UserInfo creator;
	
	private FeedActivity activity;
	
	private MediaInfo media;
	
	private MediaCommentInfo comment;
}
