package com.bravson.socialalert.domain.user.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotification {
	
	@NonNull
	private String targetUserId;

	@NonNull
	private UserNotificationType type;
	
	private String mediaUri;
	
	private String mediaTitle;
	
	private String commentId;
	
	private String commentText;
	
	@NonNull
	private String sourceUserId;
	
	@NonNull
	private String sourceUsername;
}
