package com.bravson.socialalert.domain.media;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.domain.user.privacy.LocationPrivacy;
import com.bravson.socialalert.infrastructure.util.GeoHashUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Schema(description="The meta information for the media.")
@Data
public class MediaInfo implements UserContent {

	private String mediaUri;
    
    private MediaKind kind;
    
    private String creatorId;

    private String title;
    
    @Schema(description="The media timestamp in milliseconds since the epoch.", implementation=Long.class)
	private Instant creation;
	 
    @Schema(description="The upload timestamp in milliseconds since the epoch.", implementation=Long.class)
	private Instant timestamp;
	
	private MediaFileFormat fileFormat;
	
	private MediaFileFormat previewFormat;
	
	private Integer width;
	
	private Integer height;
	
	@Schema(description="The duration of the video in milliseconds.", implementation=Long.class)
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
	
	private String category;
	
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

	private void clearLocation() {
		latitude = null;
		longitude = null;
	}
	
	public void applyLocationPrivacy() {
		if (creator == null) {
			clearLocation();
		} else if (creator.getLocationPrivacy() == LocationPrivacy.BLUR && hasLocation()) {
			blurLocation();
		} else if (creator.getLocationPrivacy() == LocationPrivacy.MASK) {
			clearLocation();
		}
	}

	private void blurLocation() {
		var newPoint = GeoHashUtil.blurLocation(latitude, longitude, LocationPrivacy.BLUR_PRECISION);
		latitude = newPoint.getLatitude();
		longitude = newPoint.getLongitude();
	}
}
