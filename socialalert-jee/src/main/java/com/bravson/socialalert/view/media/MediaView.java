package com.bravson.socialalert.view.media;

import java.io.Serializable;

import javax.enterprise.inject.Model;

import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import com.bravson.socialalert.domain.media.MediaInfo;

import lombok.Getter;
import lombok.Setter;

@Model
public class MediaView implements Serializable {

	private static final long serialVersionUID = -7844961510255752640L;

	@Getter
	@Setter
	private MediaInfo selectedMedia;

	@Getter
	private MapModel mapModel = new DefaultMapModel();
	
	public void initMapModel() {
		if (selectedMedia != null && selectedMedia.hasLocation()) {
			LatLng mapCenter = new LatLng(selectedMedia.getLatitude(), selectedMedia.getLongitude());
			mapModel.addOverlay(new Marker(mapCenter, selectedMedia.getTitle()));
		}
	}
}    
