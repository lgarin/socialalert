package com.bravson.socialalert.domain.location;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GeoBox {

	private double minLat;
	private double maxLat;
	private double minLon;
	private double maxLon;
	
	@JsonIgnore
	public double getLatDelta() {
		return maxLat - minLat;
	}

	@JsonIgnore
	public double getLonDelta() {
		return maxLon - minLon;
	}
	
	@JsonIgnore
	public double getCenterLat() {
		return (maxLat + minLat) / 2.0;
	}
	
	@JsonIgnore
	public double getCenterLon() {
		return (maxLon + minLon) / 2.0;
	}
}
