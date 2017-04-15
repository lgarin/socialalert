package com.bravson.socialalert.file.media;

import java.time.Duration;
import java.time.Instant;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Embeddable
public class MediaMetadata {

	private final Integer width;
	private final Integer height;
	private final Instant timestamp;
	private final Duration duration;
	private final Double longitude;
	private final Double latitude;
	private final String cameraMaker;
	private final String cameraModel;

	public boolean hasLocation() {
		return longitude != null && latitude != null;
	}
}
