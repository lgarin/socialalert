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
	
	public void likeMedia(String mediaUri) {
		System.out.println(selectedMedia);
		selectedMedia = mediaService.setApprovalModifier(mediaUri, ApprovalModifier.LIKE, userAccess.getUserId());
		System.out.println(selectedMedia.getUserApprovalModifier());
	}
	
	public void dislikeMedia(String mediaUri) {
		System.out.println(selectedMedia);
		selectedMedia = mediaService.setApprovalModifier(mediaUri, ApprovalModifier.DISLIKE, userAccess.getUserId());
		System.out.println(selectedMedia.getUserApprovalModifier());
	}
}    
