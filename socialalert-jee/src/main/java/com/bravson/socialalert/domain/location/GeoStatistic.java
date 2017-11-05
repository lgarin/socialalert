package com.bravson.socialalert.domain.location;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Value;

@ApiModel(description="The number of matching media in the area.")
@Value
@Builder
public class GeoStatistic {
	private double minLat;
	private double maxLat;
	private double minLon;
	private double maxLon;
	private int count;
}
