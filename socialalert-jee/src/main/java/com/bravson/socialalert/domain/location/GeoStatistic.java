package com.bravson.socialalert.domain.location;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GeoStatistic {
	private double latitude;
	private double longitude;
	private double radius;
	private int count;
}
