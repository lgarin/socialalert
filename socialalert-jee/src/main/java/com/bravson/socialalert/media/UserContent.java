package com.bravson.socialalert.media;

import com.bravson.socialalert.user.UserInfo;

public interface UserContent {

	public String getCreatorId();
	
	public UserInfo getCreator();

	public void setCreator(UserInfo userInfo);
	
}
