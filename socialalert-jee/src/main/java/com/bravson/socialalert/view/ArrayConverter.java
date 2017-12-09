package com.bravson.socialalert.view;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Named;

@Named
@ApplicationScoped
public class ArrayConverter implements Converter {
	
	@Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
    	if (value == null) {
    		return null;
    	}
        return Arrays.asList(value.split(","));
    }

    @SuppressWarnings("unchecked")
	@Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
    	if (value == null) {
    		return null;
    	}
        return ((List<String>) value).stream().collect(Collectors.joining(","));
    }

}
