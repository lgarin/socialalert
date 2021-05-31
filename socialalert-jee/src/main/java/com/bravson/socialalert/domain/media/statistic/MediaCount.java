package com.bravson.socialalert.domain.media.statistic;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.bravson.socialalert.domain.media.MediaInfo;

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
	
	@Schema(description="Number of matching media for this key")
	private long count;
	
	@Schema(description="List matching media with the highest score for this key")
	private List<MediaInfo> topMedia;
	
	public MediaCount(String key, long count) {
		this(key, count, null);
	}
}
