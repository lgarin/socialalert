package com.bravson.socialalert.file.media;

import java.time.Duration;
import java.time.Instant;

import org.bson.Document;

public interface MediaMetadata {

	String getCameraModel();

	String getCameraMaker();

	Double getLatitude();

	Double getLongitude();

	Instant getTimestamp();

	Integer getHeight();

	Integer getWidth();
	
	Duration getDuration();

	Document toBson();
	
	default boolean hasLocation() {
		return getLongitude() != null && getLatitude() != null;
	}
}
