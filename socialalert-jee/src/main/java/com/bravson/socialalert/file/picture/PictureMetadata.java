package com.bravson.socialalert.file.picture;

import java.time.Duration;
import java.time.Instant;

import org.bson.Document;

import com.bravson.socialalert.file.media.MediaMetadata;
import com.bravson.socialalert.infrastructure.util.DateUtil;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class PictureMetadata implements MediaMetadata {
	
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private Integer width;
	
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private Integer height;
	
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private Instant timestamp;

	@Getter
	@Setter(AccessLevel.PACKAGE)
	private Double longitude;
	
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private Double latitude;
	
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private String cameraMaker;
	
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private String cameraModel;
	
	public PictureMetadata(Document document) {
		width = document.getInteger("width");
		height = document.getInteger("height");
		timestamp = DateUtil.toInstant(document.getDate("timestamp"));
		longitude = document.getDouble("longitude");
		latitude = document.getDouble("latitude");
		cameraMaker = document.getString("cameraMaker");
		cameraModel = document.getString("cameraModel");
	}
	
	@Override
	public Duration getDuration() {
		return null;
	}

	protected void setDefaultTimestamp(Instant defaultTimestamp) {
		if (timestamp == null) {
			timestamp = defaultTimestamp;
		}
	}

	protected void setDefaultLatitude(Double defaultLatitude) {
		if (latitude == null) {
			latitude = defaultLatitude;
		}
	}

	protected void setDefaultLongitude(Double defaultLongitude) {
		if (longitude == null) {
			longitude = defaultLongitude;
		}
	}

	public Document toBson() {
		return new Document("width", width).append("height", height).append("timestamp", DateUtil.toDate(timestamp)).append("longitude", longitude).append("latitude", latitude).append("cameraMaker", cameraMaker).append("cameraModel", cameraModel);
	}
}
