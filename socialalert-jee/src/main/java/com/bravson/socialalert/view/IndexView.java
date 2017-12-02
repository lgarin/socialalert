package com.bravson.socialalert.view;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.map.PointSelectEvent;
import org.primefaces.event.map.StateChangeEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.user.UserInfo;

import lombok.Getter;

@Named
@SessionScoped
public class IndexView implements Serializable {

	private static final long serialVersionUID = -7844961510255752640L;

	@Getter
	private MapModel simpleModel = new DefaultMapModel();
	
	@Getter
	private LatLng center = new LatLng(46.948, 7.447);
	
	@Getter
	private int zoomLevel = 15;
	
	@Getter
	@Inject
	UserAccess userAccess;
	
	@Getter
	@Inject
	UserInfo userInfo;
	
	public void onPointSelect(PointSelectEvent event) {
        LatLng latlng = event.getLatLng();
        simpleModel.getMarkers().clear();
        simpleModel.addOverlay(new Marker(latlng, "Test Title"));
        addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "Point Selected", "Lat:" + latlng.getLat() + ", Lng:" + latlng.getLng()));
    }
	
	public void onStateChange(StateChangeEvent event) {
		center = event.getCenter();
        zoomLevel = event.getZoomLevel();
	}
	
    public void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public String getCenterString() {
    	return center.getLat() + "," + center.getLng();
    }
}    
