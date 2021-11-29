package com.bravson.socialalert.domain.feed;

import java.time.Instant;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.bravson.socialalert.domain.histogram.HistogramCount;

import lombok.Data;
import lombok.NonNull;

@Schema(description="The number of matching feed activities from a specific key.")
@Data
public class PeriodicFeedActivityCount implements HistogramCount {

	@Schema(description="The key used for grouping the feed activities")
	@NonNull
	private String key;
	
	@Schema(description="Number of matching feed activities for this key")
	private long count;
	
	@Schema(description="The grouping period in milliseconds since the epoch.", implementation=Long.class)
	@NonNull
	private Instant period;
	
	public PeriodicFeedActivityCount(@NonNull Instant period, long count) {
		this.key = period.toString();
		this.count = count;
		this.period = period;
	}
}
