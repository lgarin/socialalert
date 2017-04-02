package com.bravson.socialalert.file.media;

import static com.bravson.socialalert.infrastructure.util.DateUtil.toDate;
import static com.bravson.socialalert.infrastructure.util.DateUtil.toInstant;
import static com.bravson.socialalert.infrastructure.util.DurationUtil.toDuration;
import static com.bravson.socialalert.infrastructure.util.DurationUtil.toMillis;

import java.time.Duration;
import java.time.Instant;

import org.bson.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MediaMetadata {

	private final Integer width;
	private final Integer height;
	private final Instant timestamp;
	private final Duration duration;
	private final Double longitude;
	private final Double latitude;
	private final String cameraMaker;
	private final String cameraModel;
	
	public MediaMetadata(Document document) {
		width = document.getInteger("width");
		height = document.getInteger("height");
		duration = toDuration(document.getLong("duration"));
		timestamp = toInstant(document.getDate("timestamp"));
		longitude = document.getDouble("longitude");
		latitude = document.getDouble("latitude");
		cameraMaker = document.getString("cameraMaker");
		cameraModel = document.getString("cameraModel");
	}	

	public boolean hasLocation() {
		return longitude != null && latitude != null;
	}
	
	public Document toBson() {
		return new Document("width", width).append("height", height).append("duration", toMillis(duration)).append("timestamp", toDate(timestamp)).append("longitude", longitude).append("latitude", latitude).append("cameraMaker", cameraMaker).append("cameraModel", cameraModel);
	}
}
