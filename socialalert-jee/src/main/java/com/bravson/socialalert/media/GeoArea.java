package com.bravson.socialalert.media;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GeoArea {
	private double latitude;
	private double longitude;
	private double radius;
}
