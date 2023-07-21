package com.bravson.socialalert.infrastructure.util;

import ch.hsr.geohash.BoundingBox;
import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
import ch.hsr.geohash.util.GeoHashSizeTable;

public interface GeoHashUtil {

	public static WGS84Point blurLocation(double latitude, double longitude, int precision) {
		return GeoHash.withCharacterPrecision(latitude, longitude, precision).getBoundingBoxCenter();
	}
	
	public static int computeGeoHashPrecision(BoundingBox boundingBox, int division) {
		int precision = GeoHashSizeTable.numberOfBitsForOverlappingGeoHash(boundingBox) + Integer.highestOneBit(division) / 8;
		return (precision + 4) / 5;
	}

	public static BoundingBox computeBoundingBox(String geoHash) {
		return GeoHash.fromGeohashString(geoHash).getBoundingBox();
	}
}
