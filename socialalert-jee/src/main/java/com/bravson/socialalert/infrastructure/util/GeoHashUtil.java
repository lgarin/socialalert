package com.bravson.socialalert.infrastructure.util;

import java.util.ArrayList;
import java.util.List;

import com.bravson.socialalert.domain.location.GeoBox;

import ch.hsr.geohash.BoundingBox;
import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.util.GeoHashSizeTable;

public interface GeoHashUtil {

	public static String computeGeoHash(double latitude, double longitude, int precision) {
		return GeoHash.withCharacterPrecision(latitude, longitude, precision).toBase32();
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
	
	public static List<String> computeGeoHashList(GeoBox geoArea) {
		List<String> searchHashes = new ArrayList<>();
		BoundingBox boundingBox = toBoundingBox(geoArea);
		int precision = (GeoHashSizeTable.numberOfBitsForOverlappingGeoHash(boundingBox) + 4) / 5;
		GeoHash centerHash = GeoHash.withCharacterPrecision(geoArea.getCenterLatitude(), geoArea.getCenterLongitude(), precision);
		searchHashes.add(centerHash.toBase32());
		if (!centerHash.contains(boundingBox.getUpperLeft()) || !centerHash.contains(boundingBox.getLowerRight())) {
			for (GeoHash adjacent : centerHash.getAdjacent()) {
				BoundingBox adjacentBox = adjacent.getBoundingBox();
				if (adjacentBox.intersects(boundingBox) && !searchHashes.contains(adjacent.toBase32())) {
					boundingBox.expandToInclude(centerHash.getBoundingBox());
					searchHashes.add(adjacent.toBase32());
				}
			}
		}
		return searchHashes;
	}
}
