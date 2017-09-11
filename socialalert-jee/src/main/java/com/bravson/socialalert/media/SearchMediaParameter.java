package com.bravson.socialalert.media;

import java.time.Duration;

import lombok.Data;

@Data
public class SearchMediaParameter {

	private MediaKind mediaKind;
	private GeoArea area;
	private String keywords;
	private Duration maxAge;
	private String category;
}
