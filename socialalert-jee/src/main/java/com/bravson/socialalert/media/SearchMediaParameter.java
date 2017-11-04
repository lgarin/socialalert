package com.bravson.socialalert.media;

import java.time.Duration;

import com.bravson.socialalert.domain.location.GeoBox;

import lombok.Data;

@Data
public class SearchMediaParameter {

	private MediaKind mediaKind;
	private GeoBox area;
	private String keywords;
	private Duration maxAge;
	private String category;
}
