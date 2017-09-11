package com.bravson.socialalert.media;

import java.time.Duration;

import com.bravson.socialalert.infrastructure.rest.DurationDeserializer;
import com.bravson.socialalert.infrastructure.rest.DurationSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchMediaParameter {

	private MediaKind mediaKind;
	private GeoArea area;
	private String keywords;
	
	@ApiModelProperty(value="The maximum age in milliseconds of the searched media.", dataType="long")
	@JsonSerialize(using=DurationSerializer.class)
	@JsonDeserialize(using=DurationDeserializer.class)
	private Duration maxAge;
	
	private String category;
}
