package com.bravson.socialalert.media;

import lombok.Data;

@Data
public class GeoAddress {

	private Double latitude;
	private Double longitude;
	private String formattedAddress;
	private String locality;
	private String country;
}
