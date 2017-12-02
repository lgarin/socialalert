package com.bravson.socialalert.view.user;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.domain.user.UserInfo;

@ManagedBean
@RequestScoped
public class UserInfoProducer {
	@Inject
	UserInfoService userInfoService;
	
	@Inject
	UserAccess userAccess;
	
	@Produces
	public UserInfo readCurrentUserInfo() {
		return userInfoService.findUserInfo(userAccess.getUserId()).orElse(null);
	}
}
