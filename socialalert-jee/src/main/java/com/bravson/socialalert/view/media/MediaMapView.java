package com.bravson.socialalert.view.media;

import java.io.Serializable;
import java.util.IntSummaryStatistics;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.map.OverlaySelectEvent;
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

@ViewScoped
@Named
public class MediaMapView implements Serializable {

	private static final long serialVersionUID = 1L;

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
		addAllRectangles(parameter);
	}
	
	private void addRectangle(GeoStatistic item, int minValue, int maxValue) {
		LatLng sw = new LatLng(item.getMinLat(), item.getMinLon());
		LatLng ne = new LatLng(item.getMaxLat(), item.getMaxLon());
		Rectangle rect = new Rectangle(new LatLngBounds(ne, sw));
		rect.setData(new LatLng(item.getCenterLatitude(), item.getCenterLongitude()));
		double normalizedValue = minValue == maxValue ? 0.5 : (double) (item.getCount() - minValue) / (double) (maxValue - minValue); 
		int hue = (int) ((1.0 - normalizedValue) * 240.0);
		rect.setFillColor("hsl(" + hue + ", 100%, 50%)");
		rect.setStrokeColor("hsl(" + hue + ", 100%, 50%)");
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
		addAllRectangles(parameter);
	}

	private void addAllRectangles(SearchMediaParameter parameter) {
		List<GeoStatistic> result = searchService.groupByGeoHash(parameter);
		IntSummaryStatistics stat = result.stream().mapToInt(GeoStatistic::getCount).summaryStatistics();
		for (GeoStatistic item : result) {
			addRectangle(item, stat.getMin(), stat.getMax());
		}
	}
	
	public String getMapCenterString() {
    	return mapCenter.getLat() + "," + mapCenter.getLng();
    }
	
	public void onRectangleSelect(OverlaySelectEvent event) {
		// TODO show top media in area using carousel
	}
}
