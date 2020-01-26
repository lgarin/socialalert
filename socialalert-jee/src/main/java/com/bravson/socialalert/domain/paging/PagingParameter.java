package com.bravson.socialalert.domain.paging;

import java.time.Instant;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Value
@AllArgsConstructor
public class PagingParameter {

	@NonNull
	@Schema(description="The paging start timestamp in milliseconds since the epoch.", implementation=Long.class)
	private Instant timestamp;
	private int pageNumber;
	private int pageSize;
	
	public static PagingParameter of(Long pagingTimestamp, int pageNumber, int pageSize) {
		if (pagingTimestamp == null) {
			pagingTimestamp = System.currentTimeMillis();
		}
		return new PagingParameter(Instant.ofEpochMilli(pagingTimestamp), pageNumber, pageSize);
	}
	
	public int getOffset() {
		return pageNumber * pageSize;
	}
}
