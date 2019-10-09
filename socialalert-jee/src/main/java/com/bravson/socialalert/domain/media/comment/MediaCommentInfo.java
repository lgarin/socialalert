package com.bravson.socialalert.domain.media.comment;

import java.time.Instant;

import javax.json.bind.annotation.JsonbTypeDeserializer;
import javax.json.bind.annotation.JsonbTypeSerializer;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.bravson.socialalert.domain.media.UserContent;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.infrastructure.rest.InstantDeserializer;
import com.bravson.socialalert.infrastructure.rest.InstantSerializer;

import lombok.Data;

@Schema(description="The comment information.")
@Data
public class MediaCommentInfo implements UserContent {

	private String id;
	
	private String creatorId;
	
	@Schema(description="The media timestamp in milliseconds since the epoch.")
	@JsonbTypeSerializer(InstantSerializer.class)
	@JsonbTypeDeserializer(InstantDeserializer.class)
	private Instant creation;
	
	private String comment;
	
	private int likeCount;
	
	private int dislikeCount;
	
	private UserInfo creator;
}
