package com.bravson.socialalert.file;

import java.time.Instant;

import org.bson.Document;

import com.bravson.socialalert.infrastructure.util.DateUtil;

public class PictureMetadata {
	private Integer width;
	private Integer height;
	private Instant timestamp;
	private Double longitude;
	private Double latitude;
	private String cameraMaker;
	private String cameraModel;
	
	public PictureMetadata() {
	}
	
	public PictureMetadata(Document document) {
		width = document.getInteger("width");
		height = document.getInteger("height");
		timestamp = DateUtil.toInstant(document.getDate("timestamp"));
		longitude = document.getDouble("longitude");
		latitude = document.getDouble("latitude");
		cameraMaker = document.getString("cameraMaker");
		cameraModel = document.getString("cameraModel");
	}
	
	protected void setWidth(Integer width) {
		this.width = width;
	}

	protected void setHeight(Integer height) {
		this.height = height;
	}

	protected void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	protected void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	protected void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	protected void setCameraMaker(String cameraMaker) {
		this.cameraMaker = cameraMaker;
	}

	protected void setCameraModel(String cameraModel) {
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

	public boolean hasLocation() {
		return longitude != null && latitude != null;
	}
	
	public Document toBson() {
		return new Document("width", width).append("height", height).append("timestamp", DateUtil.toDate(timestamp)).append("longitude", longitude).append("latitude", latitude).append("cameraMaker", cameraMaker).append("cameraModel", cameraModel);
	}
}
