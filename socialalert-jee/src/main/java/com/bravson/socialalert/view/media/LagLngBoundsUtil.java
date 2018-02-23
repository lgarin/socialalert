package com.bravson.socialalert.view.media;

import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.LatLngBounds;

public interface LagLngBoundsUtil {

	static boolean equals(LatLngBounds bounds1, LatLngBounds bounds2) {
		return bounds1.getNorthEast().equals(bounds2.getNorthEast()) && bounds1.getSouthWest().equals(bounds2.getSouthWest());
	}
	
	static boolean contains(LatLngBounds bounds, LatLng point) {
		return bounds.getNorthEast().getLat() >= point.getLat() && bounds.getSouthWest().getLat() <= point.getLat() &&
				bounds.getNorthEast().getLng() >= point.getLng() && bounds.getSouthWest().getLng() <= point.getLng();
	}

	static boolean intersect(LatLngBounds bounds1, LatLngBounds bounds2) {
		return contains(bounds1, bounds2.getNorthEast()) || contains(bounds1, bounds2.getSouthWest());
	}

}
