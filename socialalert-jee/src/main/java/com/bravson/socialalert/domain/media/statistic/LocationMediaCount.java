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
	
	private String locality;
	
	private String country;
	
	public LocationMediaCount(String fullLocality, double latitude, double longitude, long count) {
		super(fullLocality, count);
		this.latitude = latitude;
		this.longitude = longitude;
		// fullLocality has the following format '<locality> [<country>]' 
		int countrySeperator = fullLocality.lastIndexOf(' ');
		locality = fullLocality.substring(0, countrySeperator);
		country = fullLocality.substring(countrySeperator + 1, fullLocality.length() - 1); 
	}
}
