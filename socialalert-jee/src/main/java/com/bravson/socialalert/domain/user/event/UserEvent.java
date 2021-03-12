package com.bravson.socialalert.domain.user.event;

import java.time.Instant;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
	
	@NonNull
	private String targetUserId;
	
	@NonNull
	@Schema(description="The event timestamp in milliseconds since the epoch.", implementation=Long.class)
	private Instant timestamp;
	
	@NonNull
	private UserEventType type;
	
	private String mediaUri;
	
	private String sourceUserId;
	
}
