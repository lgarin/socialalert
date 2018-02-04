package com.bravson.socialalert.view.media;

import java.io.Serializable;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import com.bravson.socialalert.business.media.MediaService;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.media.MediaDetail;
import com.bravson.socialalert.domain.user.approval.ApprovalModifier;
import com.bravson.socialalert.view.PageName;

import lombok.Getter;
import lombok.Setter;

@Model
public class MediaView implements Serializable {

	private static final long serialVersionUID = -7844961510255752640L;

	@Inject
	MediaService mediaService;
	
	@Inject
	UserAccess userAccess;
	
	@Getter
	@Setter
	MediaDetail selectedMedia;
	
	@Getter
	MapModel mapModel = new DefaultMapModel();
	
	public void initMapModel() {
		if (selectedMedia != null && selectedMedia.hasLocation()) {
			LatLng mapCenter = new LatLng(selectedMedia.getLatitude(), selectedMedia.getLongitude());
			mapModel.addOverlay(new Marker(mapCenter, selectedMedia.getTitle()));
		}
	}
	
	public String likeMedia() {
		selectedMedia = mediaService.setApprovalModifier(selectedMedia.getMediaUri(), ApprovalModifier.LIKE, userAccess.getUserId());
		return PageName.SHOW_MEDIA + "?faces-redirect=true&uri=" + selectedMedia.getMediaUri();
	}
	
	public String dislikeMedia() {
		selectedMedia = mediaService.setApprovalModifier(selectedMedia.getMediaUri(), ApprovalModifier.DISLIKE, userAccess.getUserId());
		return PageName.SHOW_MEDIA + "?faces-redirect=true&uri=" + selectedMedia.getMediaUri();
	}
	
	public String commentMedia() {
		return PageName.COMMENT_MEDIA + "?faces-redirect=true&uri=" + selectedMedia.getMediaUri();
	}
	
	public String editMedia() {
		return PageName.EDIT_MEDIA + "?faces-redirect=true&uri=" + selectedMedia.getMediaUri();
	}
}    
