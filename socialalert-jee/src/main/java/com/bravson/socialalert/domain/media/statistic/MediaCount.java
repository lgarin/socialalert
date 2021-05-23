package com.bravson.socialalert.domain.media.statistic;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Builder;
import lombok.Value;

@Schema(description="The number of matching media from a specific key.")
@Value
@Builder
public class MediaCount {

	@Schema(description="The key used for grouping the media")
	private String key;
	
	@Schema(description="Number of matching media from this key")
	private long count;
}
