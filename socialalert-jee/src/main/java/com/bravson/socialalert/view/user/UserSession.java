package com.bravson.socialalert.view.user;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.UserService;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.view.PageName;

import lombok.Getter;

@Named
@SessionScoped
public class UserSession implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	UserService userService;
	
	@Getter
	@Inject
	UserAccess userAccess;
	
	@Getter
	UserInfo userInfo;
	
	@Inject
	HttpServletRequest request;
	
	public void init(String token) {
		if (userInfo == null) {
			userInfo = userService.getOrCreateProfile("Bearer " + token, userAccess.getUserId(), userAccess.getIpAddress()).toOnlineUserInfo();
		}
	}
	
	public String logout() throws ServletException {
		request.logout();
		return PageName.INDEX + "?faces-redirect=true";
	}
}
