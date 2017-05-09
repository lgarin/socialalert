package com.bravson.socialalert.file.video;

import com.bravson.socialalert.infrastructure.async.AsyncEvent;

import lombok.NonNull;
import lombok.Value;

@Value
public class AsyncVideoPreviewEvent implements AsyncEvent {

	private static final long serialVersionUID = 1L;

	@NonNull
	private final String fileUri;
}
