package com.bravson.socialalert.domain.media.comment;

import java.time.Instant;

import com.bravson.socialalert.domain.media.UserContent;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.infrastructure.rest.InstantDeserializer;
import com.bravson.socialalert.infrastructure.rest.InstantSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description="The comment information.")
@Data
public class MediaCommentInfo implements UserContent {

	private String id;
	
	private String creatorId;
	
	@ApiModelProperty("The media timestamp in milliseconds since the epoch.")
	@JsonSerialize(using=InstantSerializer.class)
	@JsonDeserialize(using=InstantDeserializer.class)
	private Instant creation;
	
	private String comment;
	
	private int likeCount;
	
	private int dislikeCount;
	
	private UserInfo creator;
}
