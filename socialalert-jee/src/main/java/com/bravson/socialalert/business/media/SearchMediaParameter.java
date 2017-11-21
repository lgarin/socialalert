package com.bravson.socialalert.business.media;

import java.time.Duration;

import com.bravson.socialalert.domain.location.GeoBox;
import com.bravson.socialalert.domain.media.MediaKind;

import lombok.Data;

@Data
public class SearchMediaParameter {

	private MediaKind mediaKind;
	private GeoBox area;
	private String keywords;
	private Duration maxAge;
	private String category;
}
