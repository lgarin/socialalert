package com.bravson.socialalert.file.video;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;

import org.bson.Document;

import com.bravson.socialalert.file.media.MediaMetadata;
import com.bravson.socialalert.infrastructure.util.DateUtil;
import com.bravson.socialalert.infrastructure.util.DurationUtil;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class VideoMetadata implements MediaMetadata {
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private Integer width;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private Integer height;
	
	@Getter
	private Instant timestamp;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private Duration duration;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private Double longitude;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private Double latitude;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private String cameraMaker;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private String cameraModel;
	
	public VideoMetadata(Document document) {
		width = document.getInteger("width");
		height = document.getInteger("height");
		duration = DurationUtil.toDuration(document.getLong("duration"));
		timestamp = DateUtil.toInstant(document.getDate("timestamp"));
		longitude = document.getDouble("longitude");
		latitude = document.getDouble("latitude");
		cameraMaker = document.getString("cameraMaker");
		cameraModel = document.getString("cameraModel");
	}
	
	protected void setTimestamp(TemporalAccessor temporal) {
		this.timestamp = Instant.from(temporal);
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
		return new Document("width", width).append("height", height).append("duration", DurationUtil.toMillis(duration)).append("timestamp", DateUtil.toDate(timestamp)).append("longitude", longitude).append("latitude", latitude).append("cameraMaker", cameraMaker).append("cameraModel", cameraModel);
	}
}
