package com.bravson.socialalert.domain.media.statistic;

import java.time.Instant;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Builder;
import lombok.Value;

@Schema(description="The number of matching media from a specific period.")
@Value
@Builder
public class PeriodCount {

	@Schema(description="Number of matching media from this period")
	private long count;
	
	@Schema(description="The grouping period in milliseconds since the epoch.", implementation=Long.class)
	private Instant period;
}
