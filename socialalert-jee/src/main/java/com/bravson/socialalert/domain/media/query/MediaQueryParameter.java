package com.bravson.socialalert.domain.media.query;

import com.bravson.socialalert.domain.location.GeoArea;
import com.bravson.socialalert.domain.media.MediaConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaQueryParameter {

	@NotBlank @Size(max=MediaConstants.MAX_TITLE_LENGTH)
	@NonNull
	private String label;
	
	private double latitude;
	
	private double longitude;
	
	@PositiveOrZero
	private double radius;
	
	@Size(max=MediaConstants.MAX_TAG_LENGTH)
	private String keywords;
	
	@Size(max=MediaConstants.MAX_TAG_LENGTH)
	private String category;
	
	@Min(value = 1)
	private int hitThreshold;
	
	@JsonIgnore
	public GeoArea getLocation() {
		return GeoArea.builder()
			.latitude(getLatitude())
			.longitude(getLongitude())
			.radius(getRadius())
			.build();
	}
}
