package com.bravson.socialalert.view;

import java.time.Instant;
import java.util.Date;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Named;

import org.ocpsoft.prettytime.PrettyTime;

@Named
@ApplicationScoped
public class PrettyInstantConverter implements Converter {

	private PrettyTime formatter = new PrettyTime(Locale.ENGLISH);
	
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
    	throw new UnsupportedOperationException("getAsObject");
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
    	if (value == null) {
    		return null;
    	}
        return formatter.format(Date.from((Instant) value));
    }
}
