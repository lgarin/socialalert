package com.bravson.socialalert.view;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Named;

@Named
@ApplicationScoped
public class InstantConverter implements Converter {

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm.ss");
	
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
    	if (value == null) {
    		return null;
    	}
        return Instant.from(formatter.parse(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
    	if (value == null) {
    		return null;
    	}
        return formatter.withZone(ZoneOffset.UTC).format((Instant) value);
    }
    
}
