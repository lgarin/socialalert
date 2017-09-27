package com.bravson.socialalert.domain.paging;

import java.time.Instant;

import lombok.NonNull;
import lombok.Value;

@Value
public class PagingParameter {

	@NonNull
	private Instant timestamp;
	private int pageNumber;
	private int pageSize;
}
