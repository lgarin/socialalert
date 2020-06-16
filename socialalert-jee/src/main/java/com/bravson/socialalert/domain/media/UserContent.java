package com.bravson.socialalert.domain.media;

import com.bravson.socialalert.domain.user.UserInfo;

public interface UserContent {

	public String getCreatorId();
	
	public UserInfo getCreator();

	public void setCreator(UserInfo userInfo);
	
	public void applyLocationPrivacy();
}
