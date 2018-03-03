package com.bravson.socialalert.view.media;

import java.io.Serializable;
import java.time.Instant;
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
import com.bravson.socialalert.domain.media.MediaInfo;
import com.bravson.socialalert.domain.paging.PagingParameter;

import lombok.Getter;

@ViewScoped
@Named
public class MediaMapView implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final int SELECTED_STROKE_WEIGHT = 5;
	private static final int DEFAULT_STROKE_WEIGHT = 1;
	
	@Inject
	MediaSearchService searchService;
	
	@Getter
	MapModel mapModel = new DefaultMapModel();
	
	@Getter
	private LatLng mapCenter = new LatLng(0.0, 0.0);
	
	@Getter
	private int mapZoomLevel = 2;
	
	@Getter
	private List<MediaInfo> topMediaList;
	
	private Rectangle selectedRectangle;

	@PostConstruct
	void init() {
		SearchMediaParameter parameter = new SearchMediaParameter();
		fillTopMediaList(parameter);
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
		if (selectedRectangle != null && LagLngBoundsUtil.equals(selectedRectangle.getBounds(), rect.getBounds())) {
			rect.setStrokeWeight(SELECTED_STROKE_WEIGHT);
			selectedRectangle = rect;
		} else {
			rect.setStrokeWeight(DEFAULT_STROKE_WEIGHT);
		}
		mapModel.addOverlay(rect);
	}
	
	public void onMapStateChange(StateChangeEvent event) {
		mapCenter = event.getCenter();
		mapZoomLevel = event.getZoomLevel();
		LatLngBounds mapBounds = event.getBounds();
		SearchMediaParameter parameter = buildSearchParameter(mapBounds);
		
		if (selectedRectangle != null && !LagLngBoundsUtil.intersect(mapBounds, selectedRectangle.getBounds())) {
			selectedRectangle = null;
		}
		
		if (selectedRectangle == null) {
			fillTopMediaList(parameter);
		}
		
		mapModel.getRectangles().clear();
		addAllRectangles(parameter);
	}

	private SearchMediaParameter buildSearchParameter(LatLngBounds mapBounds) {
		GeoBox geoBox = GeoBox.builder()
						.maxLat(mapBounds.getNorthEast().getLat())
						.minLat(mapBounds.getSouthWest().getLat())
						.maxLon(mapBounds.getNorthEast().getLng())
						.minLon(mapBounds.getSouthWest().getLng())
						.build();
		SearchMediaParameter parameter = new SearchMediaParameter();
		parameter.setArea(geoBox);
		return parameter;
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
		if (selectedRectangle != null) {
			selectedRectangle.setStrokeWeight(DEFAULT_STROKE_WEIGHT);
		}
		selectedRectangle = (Rectangle) event.getOverlay();
		
		selectedRectangle.setStrokeWeight(SELECTED_STROKE_WEIGHT);
		SearchMediaParameter parameter = buildSearchParameter(selectedRectangle.getBounds());
		fillTopMediaList(parameter);
	}

	private void fillTopMediaList(SearchMediaParameter parameter) {
		topMediaList = searchService.searchMedia(parameter, new PagingParameter(Instant.now(), 0, 20)).getContent();
	}
}
