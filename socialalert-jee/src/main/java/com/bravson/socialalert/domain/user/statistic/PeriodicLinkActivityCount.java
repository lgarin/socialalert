package com.bravson.socialalert.domain.user.statistic;

import java.time.Instant;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Data;
import lombok.NonNull;

@Schema(description="The number of matching link activities from a specific key.")
@Data
public class PeriodicLinkActivityCount {

	@Schema(description="The key used for grouping the link activities")
	@NonNull
	private String key;
	
	@Schema(description="Number of matching link activities for this key")
	private long count;
	
	@Schema(description="The grouping period in milliseconds since the epoch.", implementation=Long.class)
	@NonNull
	private Instant period;
	
	public PeriodicLinkActivityCount(@NonNull Instant period, long count) {
		this.key = period.toString();
		this.count = count;
		this.period = period;
	}
}
