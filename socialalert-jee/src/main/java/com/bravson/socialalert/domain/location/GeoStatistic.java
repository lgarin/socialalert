package com.bravson.socialalert.domain.location;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Value;

@Schema(description="The number of matching media in the area.")
@Value
@Builder
public class GeoStatistic {
	
	@Schema(description="Lower latitude of the geo box")
	private double minLat;
	
	@Schema(description="Upper latitude of the geo box")
	private double maxLat;
	
	@Schema(description="Lower longitude of the geo box")
	private double minLon;
	
	@Schema(description="Upper longitude of the geo box")
	private double maxLon;
	
	@Schema(description="Number of media in this geo box")
	private long count;
	
	@Schema(description="Number of media with a feeling rating")
	private long feelingCount;
	
	@Schema(description="Sum of all feeling ratings")
	private long feelingSum;
	
	@JsonIgnore
	public double getCenterLat() {
		return (maxLat + minLat) / 2.0;
	}
	
	@JsonIgnore
	public double getCenterLon() {
		return (maxLon + minLon) / 2.0;
	}

	public boolean intersect(GeoBox area) {
		if (area == null) {
			return true;
		}
		return area.getMinLon() <= maxLon && area.getMaxLon() >= minLon && area.getMinLat() <= maxLat && area.getMaxLat() >= minLat;
	}
}
