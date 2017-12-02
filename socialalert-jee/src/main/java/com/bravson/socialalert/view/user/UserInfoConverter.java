package com.bravson.socialalert.view.user;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.domain.user.UserInfo;

@Named
@ApplicationScoped
public class UserInfoConverter  implements Converter {
	
	@Inject
	UserInfoService userInfoService;

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		return ((UserInfo) value).getId();
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null) {
			return null;
		}
		return userInfoService.findUserInfo(value).orElse(null);
	}

}
