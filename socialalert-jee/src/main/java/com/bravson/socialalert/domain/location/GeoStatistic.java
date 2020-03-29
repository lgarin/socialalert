package com.bravson.socialalert.domain.location;

import javax.json.bind.annotation.JsonbTransient;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

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
	private long count;
	
	@JsonbTransient
	public double getCenterLat() {
		return (maxLat + minLat) / 2.0;
	}
	
	@JsonbTransient
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
