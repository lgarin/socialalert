package com.bravson.socialalert.business.file.media;

import java.time.Duration;
import java.time.Instant;

import javax.persistence.Embeddable;

import org.hibernate.search.mapper.pojo.bridge.builtin.annotation.GeoPointBinding;
import org.hibernate.search.mapper.pojo.bridge.builtin.annotation.Latitude;
import org.hibernate.search.mapper.pojo.bridge.builtin.annotation.Longitude;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

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
@GeoPointBinding(fieldName = "position")
public class MediaMetadata {

	@NonNull
	@GenericField
	private Integer width;
	@NonNull
	@GenericField
	private Integer height;
	@GenericField
	private Instant timestamp;
	@GenericField
	private Duration duration;
	@Latitude
	private Double latitude;
	@Longitude
	private Double longitude;
	@KeywordField
	private String cameraMaker;
	@KeywordField
	private String cameraModel;

	public boolean hasLocation() {
		return latitude != null && longitude != null;
	}
}
