package com.bravson.socialalert.domain.file;

import java.time.Duration;
import java.time.Instant;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.bravson.socialalert.domain.media.UserContent;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.domain.user.privacy.LocationPrivacy;
import com.bravson.socialalert.infrastructure.util.GeoHashUtil;

import lombok.Data;

@Schema(description="The meta information for the file.")
@Data
public class FileInfo implements UserContent {

	private String fileUri;
	
	private Long contentSize;
	
	private MediaFileFormat fileFormat;
	
	private MediaFileFormat previewFormat;
	
	@Schema(description="The media timestamp in milliseconds since the epoch.", implementation=Long.class)
	private Instant creation;
	 
	@Schema(description="The upload timestamp in milliseconds since the epoch.", implementation=Long.class)
	private Instant timestamp;
	
	private Integer width;
	
	private Integer height;
	
	@Schema(description="The duration of the video in milliseconds.", implementation=Long.class)
	private Duration duration;
	
	private Double longitude;
	
	private Double latitude;
	
	private String cameraMaker;
	
	private String cameraModel;
    
	private String creatorId;
    
	private UserInfo creator;
	
	public boolean isVideo() {
		return MediaFileFormat.VIDEO_SET.contains(fileFormat);
	}
	
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
