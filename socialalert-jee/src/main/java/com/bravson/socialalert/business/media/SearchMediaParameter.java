package com.bravson.socialalert.business.media;

import java.time.Duration;

import com.bravson.socialalert.domain.location.GeoArea;
import com.bravson.socialalert.domain.location.GeoBox;
import com.bravson.socialalert.domain.media.MediaKind;

import lombok.Data;

@Data
public class SearchMediaParameter {

	private MediaKind mediaKind;
	private GeoBox area;
	private GeoArea location;
	private String keywords;
	private Duration maxAge;
	private String category; // TODO should be a list of String
	private String creator;
}
