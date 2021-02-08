package com.bravson.socialalert.domain.media.comment;

import java.time.Instant;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.bravson.socialalert.domain.media.UserContent;
import com.bravson.socialalert.domain.user.UserInfo;

import lombok.Data;

@Schema(description="The comment information.")
@Data
public class MediaCommentInfo implements UserContent {

	private String id;
	
	private String creatorId;
	
	@Schema(description="The media timestamp in milliseconds since the epoch.", implementation = Long.class)
	private Instant creation;
	
	private String comment;
	
	private int likeCount;
	
	private int dislikeCount;
	
	private UserInfo creator;
	
	public void applyPrivacy() {
	}
}
