package com.bravson.socialalert.business.file.media;

import java.time.Duration;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.search.mapper.pojo.bridge.builtin.annotation.GeoPointBinding;
import org.hibernate.search.mapper.pojo.bridge.builtin.annotation.Latitude;
import org.hibernate.search.mapper.pojo.bridge.builtin.annotation.Longitude;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.bravson.socialalert.domain.location.GeoAddress;
import com.bravson.socialalert.infrastructure.entity.FieldLength;

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
@GeoPointBinding(fieldName = "position")
public class MediaMetadata {

	@NonNull
	@Column(name = "width")
	@GenericField
	private Integer width;
	@NonNull
	@Column(name = "height")
	@GenericField
	private Integer height;
	@Column(name = "media_timestamp")
	@GenericField
	private Instant timestamp;
	@GenericField
	private Duration duration;
	@Latitude
	private Double latitude;
	@Longitude
	private Double longitude;
	@Column(name = "camera_maker", length = FieldLength.NAME)
	@KeywordField
	private String cameraMaker;
	@Column(name = "camera_model", length = FieldLength.NAME)
	@KeywordField
	private String cameraModel;

	public boolean hasLocation() {
		return latitude != null && longitude != null;
	}
	
	public GeoAddress getLocation() {
		return GeoAddress.builder().latitude(latitude).longitude(longitude).build();
	}
}
