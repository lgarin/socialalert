package com.bravson.socialalert.domain.media.statistic;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Schema(description="Aggregation for the media statitics.")
@Data
@AllArgsConstructor
@Builder
public class MediaStatisticAggregation {

	private long pictureCount;
	private long videoCount;
	private long totalHitCount;
	private long totalLikeCount;
	private long totalDislikeCount;
	private long totalCommentCount;
	private long distinctUserCount;
	private long distinctCountryCount;
	private double averageFeeling;
}
