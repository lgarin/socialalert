package com.bravson.socialalert.domain.paging;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Value
@AllArgsConstructor
public class PagingParameter {

	@NonNull
	private Instant timestamp;
	private int pageNumber;
	private int pageSize;
	
	public static PagingParameter of(Long pagingTimestamp, int pageNumber, int pageSize) {
		if (pagingTimestamp == null) {
			pagingTimestamp = System.currentTimeMillis();
		}
		return new PagingParameter(Instant.ofEpochMilli(pagingTimestamp), pageNumber, pageSize);
	}
}
