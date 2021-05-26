package com.bravson.socialalert.domain.media.statistic;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description="The number of matching media from a specific location.")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LocationMediaCount extends MediaCount {

	private double latitude;
	
	private double longitude;
	
	public LocationMediaCount(String fullLocality, double latitude, double longitude, long count) {
		super(fullLocality, count);
		this.latitude = latitude;
		this.longitude = longitude;
	}
}
