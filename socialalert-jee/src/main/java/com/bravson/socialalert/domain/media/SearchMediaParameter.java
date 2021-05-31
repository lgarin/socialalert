package com.bravson.socialalert.domain.media;

import java.time.Duration;

import com.bravson.socialalert.domain.location.GeoArea;
import com.bravson.socialalert.domain.location.GeoBox;

import lombok.Data;

@Data
public class SearchMediaParameter {

	private MediaKind mediaKind;
	private GeoBox area;
	private GeoArea location;
	private String keywords;
	private Duration maxAge;
	private String category;
	private String creator;
	private String locality;
	private String country;
}
