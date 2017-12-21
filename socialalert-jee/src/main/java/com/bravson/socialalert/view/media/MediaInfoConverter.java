package com.bravson.socialalert.view.media;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

import com.bravson.socialalert.business.media.MediaSearchService;
import com.bravson.socialalert.domain.media.MediaInfo;

@Named
@ApplicationScoped
public class MediaInfoConverter implements Converter {
	
	@Inject
	MediaSearchService mediaSearchService;

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		return ((MediaInfo) value).getMediaUri();
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null) {
			return null;
		}
		return mediaSearchService.findMediaByUri(value).orElse(null);
	}
}
