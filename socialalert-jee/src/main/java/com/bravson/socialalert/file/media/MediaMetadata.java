package com.bravson.socialalert.file.media;

import java.time.Duration;
import java.time.Instant;

import javax.persistence.Convert;
import javax.persistence.Embeddable;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Latitude;
import org.hibernate.search.annotations.Longitude;
import org.hibernate.search.annotations.Spatial;
import org.hibernate.search.annotations.SpatialMode;

import com.bravson.socialalert.infrastructure.entity.DurationAttributeConverter;
import com.bravson.socialalert.infrastructure.entity.InstantAttributeConverter;

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
@Indexed
@Spatial(name="coordinates", spatialMode=SpatialMode.HASH)
public class MediaMetadata {

	@NonNull
	private Integer width;
	@NonNull
	private Integer height;
	@Convert(converter=InstantAttributeConverter.class)
	private Instant timestamp;
	@Convert(converter=DurationAttributeConverter.class)
	private Duration duration;
	@Longitude(of="coordinates")
	private Double longitude;
	@Latitude(of="coordinates")
	private Double latitude;
	@Field
	private String cameraMaker;
	@Field
	private String cameraModel;

	public boolean hasLocation() {
		return longitude != null && latitude != null;
	}
}
