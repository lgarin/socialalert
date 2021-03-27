package com.bravson.socialalert.infrastructure.util;

import com.bravson.socialalert.domain.location.GeoBox;

import ch.hsr.geohash.BoundingBox;
import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
import ch.hsr.geohash.util.GeoHashSizeTable;

public interface GeoHashUtil {

	public static WGS84Point blurLocation(double latitude, double longitude, int precision) {
		return GeoHash.withCharacterPrecision(latitude, longitude, precision).getBoundingBoxCenterPoint();
	}
	
	private static BoundingBox toBoundingBox(GeoBox geoArea) {
		return new BoundingBox(geoArea.getMinLat(), geoArea.getMaxLat(), geoArea.getMinLon(), geoArea.getMaxLon());
	}
	
	public static int computeGeoHashPrecision(GeoBox geoArea, int division) {
		BoundingBox boundingBox = toBoundingBox(geoArea);
		int precision = GeoHashSizeTable.numberOfBitsForOverlappingGeoHash(boundingBox) + Integer.highestOneBit(division) / 8;
		return (precision + 4) / 5;
	}

	public static GeoBox computeBoundingBox(String geoHash) {
		BoundingBox box = GeoHash.fromGeohashString(geoHash).getBoundingBox();
		return GeoBox.builder().minLon(box.getMinLon()).maxLon(box.getMaxLon()).minLat(box.getMinLat()).maxLat(box.getMaxLat()).build();
	}
}
