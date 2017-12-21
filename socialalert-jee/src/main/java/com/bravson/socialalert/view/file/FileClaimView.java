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
import com.bravson.socialalert.business.media.MediaUpsertService;
import com.bravson.socialalert.business.media.UpsertMediaParameter;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.file.FileInfo;
import com.bravson.socialalert.domain.location.GeoAddress;
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
	
	@Inject
	MediaUpsertService mediaUpsertService;
	
	@Inject
	UserAccess userAccess;
	
	public void startConversation() {
		if (conversation.isTransient()) {
			conversation.begin();
		}
	}
	
	public String updateLocation() {
		if (mapCenter == null) {
			if (selectedFile.hasLocation()) {
				mapCenter = new LatLng(selectedFile.getLatitude(), selectedFile.getLongitude());
				mapModel.addOverlay(new Marker(mapCenter, title));
			} else {
				mapCenter = new LatLng(46.948, 7.447);
			}
		}
		return PageName.CLAIM_LOCATION + "?faces-redirect=true";
	}
	
	public String updateMetadata() {
		return PageName.CLAIM_FILE + "?faces-redirect=true";
	}
	
	public String confirmPublish() {
		return PageName.CLAIM_CONFIRMATION + "?faces-redirect=true";
	}
	
	public String publish() {
		UpsertMediaParameter param = UpsertMediaParameter.builder()
				.title(title).description(description)
				.categories(categories).tags(tags)
				.location(GeoAddress.builder().latitude(mapCenter.getLat()).longitude(mapCenter.getLng()).build())
				.build();
		mediaUpsertService.claimMedia(selectedFile.getFileUri(), param, userAccess);
		conversation.end();
		return PageName.INDEX + "?faces-redirect=true";
	}
	
	public void onMapPointSelect(PointSelectEvent event) {
		mapCenter = event.getLatLng();
        mapModel.getMarkers().clear();
        mapModel.addOverlay(new Marker(mapCenter, title));
        addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "Position selected", "Lat:" + mapCenter.getLat() + ", Lng:" + mapCenter.getLng()));
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
