package com.bravson.socialalert.domain.location;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Schema(description="The number of matching media in the area.")
@Value
@Builder
public class GeoStatistic {
	
	private double minLat;
	private double maxLat;
	private double minLon;
	private double maxLon;
	private int count;
	
	public double getCenterLatitude() {
		return (maxLat + minLat) / 2.0;
	}
	
	public double getCenterLongitude() {
		return (maxLon + minLon) / 2.0;
	}

	public boolean intersect(GeoBox area) {
		if (area == null) {
			return true;
		}
		return area.getMinLon() <= maxLon && area.getMaxLon() >= minLon && area.getMinLat() <= maxLat && area.getMaxLat() >= minLat;
	}
}
