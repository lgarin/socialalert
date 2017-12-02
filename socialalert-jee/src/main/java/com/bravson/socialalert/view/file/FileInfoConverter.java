package com.bravson.socialalert.view.file;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

import com.bravson.socialalert.business.file.FileSearchService;
import com.bravson.socialalert.domain.file.FileInfo;

@Named
@ApplicationScoped
public class FileInfoConverter implements Converter {
	
	@Inject
	FileSearchService fileSearchService;

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		return ((FileInfo) value).getFileUri();
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null) {
			return null;
		}
		return fileSearchService.findFileByUri(value).orElse(null);
	}
}
