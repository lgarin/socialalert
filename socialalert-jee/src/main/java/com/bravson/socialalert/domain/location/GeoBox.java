package com.bravson.socialalert.domain.location;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GeoBox {

	private double minLat;
	private double maxLat;
	private double minLon;
	private double maxLon;
	
	public double getLatitudeSize() {
		return maxLat - minLat;
	}

	public double getLongitudeSize() {
		return maxLon - minLon;
	}
	
	public double getCenterLatitude() {
		return (maxLat + minLat) / 2.0;
	}
	
	public double getCenterLongitude() {
		return (maxLon + minLon) / 2.0;
	}
}
