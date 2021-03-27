package com.bravson.socialalert.test.util;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import com.bravson.socialalert.domain.location.GeoBox;
import com.bravson.socialalert.infrastructure.util.GeoHashUtil;

public class GeoHashUtilTest extends Assertions {
	
	 @Test
	 public void convertGeoHash() {
		 GeoBox geoBox = GeoHashUtil.computeBoundingBox("u0m9");
		 assertThat(geoBox.getMaxLat()).isEqualTo(46.75, Offset.offset(0.01));
		 assertThat(geoBox.getMinLat()).isEqualTo(46.58, Offset.offset(0.01));
		 assertThat(geoBox.getMaxLon()).isEqualTo(8.08, Offset.offset(0.01));
		 assertThat(geoBox.getMinLon()).isEqualTo(7.73, Offset.offset(0.01));
		 
		 String geoHash = GeoHashUtil.computeGeoHash(geoBox.getCenterLat(), geoBox.getCenterLon(), 4);
		 assertThat(geoHash).isEqualTo("u0m9");
	 }

	 @Test
	 public void computeGeoHashPrecision() {
		 GeoBox geoBox = GeoBox.builder().maxLat(46.89713427117885).minLat(46.123042072708216)
				 .maxLon(8.526546992361546).minLon(7.396524623036385).build();
		 int precision = GeoHashUtil.computeGeoHashPrecision(geoBox, 128);
		 assertThat(precision).isEqualTo(7);
	 }
	 
	 @Test
	 public void computeGeoHashList() {
		 GeoBox geoBox = GeoBox.builder().maxLat(46.89713427117885).minLat(46.123042072708216)
				 .maxLon(8.526546992361546).minLon(7.396524623036385).build();
		 List<String> result = GeoHashUtil.computeGeoHashList(geoBox, 8);
		 assertThat(result).hasSize(20)
		 	.contains("u0m3", "u0m9", "u0mc", "u0m2", "u0m8", "u0mb", "u0jr", "u0jx", "u0jz")
		 	.doesNotContain("u0me", "u0jt", "u0q2", "u0kb");
	 }
}
