package com.bravson.socialalert.file.media;

import java.time.Duration;
import java.time.Instant;

import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@Embeddable
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
public class MediaMetadata {

	@NonNull
	private Integer width;
	@NonNull
	private Integer height;
	private Instant timestamp;
	private Duration duration;
	private Double longitude;
	private Double latitude;
	private String cameraMaker;
	private String cameraModel;

	public boolean hasLocation() {
		return longitude != null && latitude != null;
	}
}
