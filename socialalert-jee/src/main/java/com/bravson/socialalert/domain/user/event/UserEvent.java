package com.bravson.socialalert.domain.user.event;

import java.time.Instant;

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
	private Instant timestamp;
	
	@NonNull
	private UserEventType type;
	
	private String mediaUri;
	
	private String sourceUserId;
	
	
}
