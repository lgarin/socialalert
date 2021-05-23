package com.bravson.socialalert.domain.media.statistic;

import java.time.Instant;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@Schema(description="The number of matching media from a specific period.")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PeriodicMediaCount extends MediaCount {
	
	@Schema(description="The grouping period in milliseconds since the epoch.", implementation=Long.class)
	@NonNull
	private Instant period;
	
	public PeriodicMediaCount(@NonNull Instant period, long count) {
		super(period.toString(), count);
		this.period = period;
	}
}
