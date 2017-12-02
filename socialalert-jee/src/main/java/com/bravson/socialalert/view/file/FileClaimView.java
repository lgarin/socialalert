package com.bravson.socialalert.view.file;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.primefaces.event.map.PointSelectEvent;
import org.primefaces.event.map.StateChangeEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import com.bravson.socialalert.business.media.MediaConstants;
import com.bravson.socialalert.domain.file.FileInfo;
import com.bravson.socialalert.view.PageName;

import lombok.Getter;
import lombok.Setter;

@Named
@ConversationScoped
public class FileClaimView implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter @Setter
	@NotEmpty @Size(max=MediaConstants.MAX_TITLE_LENGTH)
	private String title;
	
	@Getter @Setter
	@Size(max=MediaConstants.MAX_DESCRIPTION_LENGTH)
	private String description;
	
	@Getter @Setter
	@Size(max=MediaConstants.MAX_CATEGORY_COUNT)
	private List<String> categories;
	
	@Getter @Setter
	@Size(max=MediaConstants.MAX_TAG_COUNT)
	private List<String> tags;
	
	@Inject
	Conversation conversation;
	
	@Getter @Setter
	FileInfo selectedFile;
	
	@Getter
	private MapModel mapModel = new DefaultMapModel();
	
	@Getter
	private LatLng mapCenter;
	
	@Getter
	private int mapZoomLevel = 15;
	
	public String updateLocation() {
		if (conversation.isTransient()) {
			if (selectedFile.hasLocation()) {
				mapCenter = new LatLng(selectedFile.getLatitude(), selectedFile.getLongitude());
				mapModel.addOverlay(new Marker(mapCenter, title));
			} else {
				mapCenter = new LatLng(46.948, 7.447);
			}
			conversation.begin();
		}
		return PageName.CLAIM_LOCATION;
	}
	
	public String updateMetadata() {
		return PageName.CLAIM_FILE;
	}
	
	public String confirmPublish() {
		conversation.end();
		return PageName.INDEX;
	}
	
	public void onMapPointSelect(PointSelectEvent event) {
        LatLng latlng = event.getLatLng();
        mapModel.getMarkers().clear();
        mapModel.addOverlay(new Marker(latlng, title));
        addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "Position selected", "Lat:" + latlng.getLat() + ", Lng:" + latlng.getLng()));
    }
	
	public void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
	
	public void onMapStateChange(StateChangeEvent event) {
		mapCenter = event.getCenter();
        mapZoomLevel = event.getZoomLevel();
	}
	
	public String getMapCenterString() {
    	return mapCenter.getLat() + "," + mapCenter.getLng();
    }
}
