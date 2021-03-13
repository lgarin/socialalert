package com.bravson.socialalert.domain.user.notification;

public interface SseUserNotification {

	String getId();
	
	String getEvent();
	
	UserNotification getData();
}
