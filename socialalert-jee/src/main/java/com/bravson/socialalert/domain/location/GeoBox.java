package com.bravson.socialalert.domain.location;

import javax.json.bind.annotation.JsonbTransient;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GeoBox {

	private double minLat;
	private double maxLat;
	private double minLon;
	private double maxLon;
	
	@JsonbTransient
	public double getLatDelta() {
		return maxLat - minLat;
	}

	@JsonbTransient
	public double getLonDelta() {
		return maxLon - minLon;
	}
	
	@JsonbTransient
	public double getCenterLat() {
		return (maxLat + minLat) / 2.0;
	}
	
	@JsonbTransient
	public double getCenterLon() {
		return (maxLon + minLon) / 2.0;
	}
}
