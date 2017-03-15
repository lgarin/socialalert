package com.bravson.socialalert.file;

import java.time.Instant;

public class PictureMetadata {
	private Integer width;
	private Integer height;
	private Instant timestamp;
	private Double longitude;
	private Double latitude;
	private String cameraMaker;
	private String cameraModel;
	
	public void setWidth(Integer width) {
		this.width = width;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setCameraMaker(String cameraMaker) {
		this.cameraMaker = cameraMaker;
	}

	public void setCameraModel(String cameraModel) {
		this.cameraModel = cameraModel;
	}

	public Integer getWidth() {
		return width;
	}

	public Integer getHeight() {
		return height;
	}

	public Instant getTimestamp() {
		return timestamp;
	}
	
	public Double getLongitude() {
		return longitude;
	}
	public Double getLatitude() {
		return latitude;
	}
	public String getCameraMaker() {
		return cameraMaker;
	}
	public String getCameraModel() {
		return cameraModel;
	}

	public void setDefaultTimestamp(Instant defaultTimestamp) {
		if (timestamp == null) {
			timestamp = defaultTimestamp;
		}
	}

	public void setDefaultLatitude(Double defaultLatitude) {
		if (latitude == null) {
			latitude = defaultLatitude;
		}
	}

	public void setDefaultLongitude(Double defaultLongitude) {
		if (longitude == null) {
			longitude = defaultLongitude;
		}
	}

	public boolean hasLocation() {
		return longitude != null && latitude != null;
	}
}
