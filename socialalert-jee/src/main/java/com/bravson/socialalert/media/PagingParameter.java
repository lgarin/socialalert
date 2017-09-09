package com.bravson.socialalert.media;

import java.time.Instant;

import javax.validation.constraints.NotNull;

import lombok.NonNull;
import lombok.Value;

@Value
public class PagingParameter {

	@NonNull
	@NotNull
	private Instant timestamp;
	private int pageNumber;
	private int pageSize;
}
