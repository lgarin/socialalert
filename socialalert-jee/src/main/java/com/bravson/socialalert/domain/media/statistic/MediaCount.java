package com.bravson.socialalert.domain.media.statistic;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Schema(description="The number of matching media from a specific key.")
@Data
@AllArgsConstructor
public class MediaCount {

	@Schema(description="The key used for grouping the media")
	@NonNull
	private String key;
	
	@Schema(description="Number of matching media from this key")
	private long count;
}
