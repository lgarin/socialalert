package com.bravson.socialalert.view.media;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

import com.bravson.socialalert.business.media.MediaService;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.media.MediaDetail;

@Named
@ApplicationScoped
public class MediaDetailConverter implements Converter {
	
	@Inject
	MediaService mediaService;
	
	@Inject
	UserAccess userAccess;

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		return ((MediaDetail) value).getMediaUri();
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null) {
			return null;
		}
		return mediaService.viewMediaDetail(value, userAccess.getUserId());
	}
}
