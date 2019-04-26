package com.bravson.socialalert.domain.media;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.infrastructure.rest.DurationDeserializer;
import com.bravson.socialalert.infrastructure.rest.DurationSerializer;
import com.bravson.socialalert.infrastructure.rest.InstantDeserializer;
import com.bravson.socialalert.infrastructure.rest.InstantSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description="The meta information for the media.")
@Data
public class MediaInfo implements UserContent {

	private String mediaUri;
    
    private MediaKind kind;
    
    private String creatorId;

    private String title;
    
    private String description;
	
    @Schema(description="The media timestamp in milliseconds since the epoch.")
	@JsonSerialize(using=InstantSerializer.class)
	@JsonDeserialize(using=InstantDeserializer.class)
	private Instant creation;
	 
    @Schema(description="The upload timestamp in milliseconds since the epoch.")
	@JsonSerialize(using=InstantSerializer.class)
	@JsonDeserialize(using=InstantDeserializer.class)
	private Instant timestamp;
	
	private MediaFileFormat fileFormat;
	
	private MediaFileFormat previewFormat;
	
	@Schema(description="The duration of the video in milliseconds.", implementation=Long.class)
	@JsonSerialize(using=DurationSerializer.class)
	@JsonDeserialize(using=DurationDeserializer.class)
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
	
	private UserInfo creator;

	@JsonIgnore
	public boolean isVideo() {
		return MediaFileFormat.VIDEO_SET.contains(fileFormat);
	}
	
	@JsonIgnore
	public boolean isPicture() {
		return MediaFileFormat.PICTURE_SET.contains(fileFormat);
	}
	
	public boolean hasVideoPreview() {
		return MediaFileFormat.VIDEO_SET.contains(previewFormat);
	}

	public boolean hasLocation() {
		return latitude != null && longitude != null;
	}
}
