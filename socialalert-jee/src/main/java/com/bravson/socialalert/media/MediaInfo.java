package com.bravson.socialalert.media;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.bravson.socialalert.infrastructure.rest.InstantDeserializer;
import com.bravson.socialalert.infrastructure.rest.InstantSerializer;
import com.bravson.socialalert.user.UserInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
public class MediaInfo implements UserContent, Serializable {

	private static final long serialVersionUID = 1L;

	private String mediaUri;
    
    private MediaType type;
    
    private String creatorId;

    private String title;
    
    private String description;
	
	@JsonSerialize(using=InstantSerializer.class)
	@JsonDeserialize(using=InstantDeserializer.class)
	private Instant creation;
	 
	@JsonSerialize(using=InstantSerializer.class)
	@JsonDeserialize(using=InstantDeserializer.class)
	private Instant timestamp;
	
	private Integer width;
	
	private Integer height;
	
	private Duration duration;

	private Double longitude;
	
	private Double latitude;
	
	private String locality;
	
	private String country;
	
	private String cameraMaker;
	
	private String cameraModel;
	
	private int hitCount;
	
	private int likeCount;
	
	private int dislikeCount;
	
	private int commentCount;
	
	private List<String> categories;
	
	private List<String> tags;
	
	private ApprovalModifier userApprovalModifier;
	
	private UserInfo creator;

}
