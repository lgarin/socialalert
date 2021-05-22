package com.bravson.socialalert.domain.media.statistic;

import java.time.Instant;

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
	
	@Schema(description="The grouping period in milliseconds since the epoch.", implementation=Long.class)
	private Instant period;
}
