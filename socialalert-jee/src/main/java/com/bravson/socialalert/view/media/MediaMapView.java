package com.bravson.socialalert.view.media;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.primefaces.event.map.StateChangeEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.LatLngBounds;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Rectangle;

import com.bravson.socialalert.business.media.MediaSearchService;
import com.bravson.socialalert.business.media.SearchMediaParameter;
import com.bravson.socialalert.domain.location.GeoBox;
import com.bravson.socialalert.domain.location.GeoStatistic;

import lombok.Getter;

@Model
public class MediaMapView {

	@Inject
	MediaSearchService searchService;
	
	@Getter
	MapModel mapModel = new DefaultMapModel();
	
	@Getter
	private LatLng mapCenter = new LatLng(0.0, 0.0);
	
	@Getter
	private int mapZoomLevel = 2;

	@PostConstruct
	void init() {
		SearchMediaParameter parameter = new SearchMediaParameter();
		for (GeoStatistic item : searchService.groupByGeoHash(parameter)) {
			addRectangle(item);
		}
	}
	
	private void addRectangle(GeoStatistic item) {
		LatLng sw = new LatLng(item.getMinLat(), item.getMinLon());
		LatLng ne = new LatLng(item.getMaxLat(), item.getMaxLon());
		Rectangle rect = new Rectangle(new LatLngBounds(ne, sw));
		rect.setData(item.getCount());
		rect.setStrokeColor("#d93c3c");
		rect.setFillColor("#d93c3c");
		rect.setFillOpacity(0.5);
		mapModel.addOverlay(rect);
	}
	
	public void onMapStateChange(StateChangeEvent event) {
		mapCenter = event.getCenter();
		mapZoomLevel = event.getZoomLevel();
		LatLngBounds mapBounds = event.getBounds();
		GeoBox geoBox = GeoBox.builder()
						.maxLat(mapBounds.getNorthEast().getLat())
						.minLat(mapBounds.getSouthWest().getLat())
						.maxLon(mapBounds.getNorthEast().getLng())
						.minLon(mapBounds.getSouthWest().getLng())
						.build();
		SearchMediaParameter parameter = new SearchMediaParameter();
		parameter.setArea(geoBox);
		
		mapModel.getRectangles().clear();
		for (GeoStatistic item : searchService.groupByGeoHash(parameter)) {
			addRectangle(item);
		}
	}
	
	public String getMapCenterString() {
    	return mapCenter.getLat() + "," + mapCenter.getLng();
    }
}
