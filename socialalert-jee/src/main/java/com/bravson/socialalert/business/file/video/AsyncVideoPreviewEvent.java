package com.bravson.socialalert.business.file.video;

import com.bravson.socialalert.infrastructure.async.AsyncEvent;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor="of")
public class AsyncVideoPreviewEvent implements AsyncEvent {

	private static final long serialVersionUID = 1L;

	@NonNull
	private final String fileUri;
}
