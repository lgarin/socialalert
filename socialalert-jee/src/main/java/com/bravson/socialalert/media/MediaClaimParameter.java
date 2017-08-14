package com.bravson.socialalert.media;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public final class MediaClaimParameter {

	@NonNull
	private String fileUri;
	
	@NonNull
	private String userId;
	
	@NonNull
	private String ipAddress;
}
