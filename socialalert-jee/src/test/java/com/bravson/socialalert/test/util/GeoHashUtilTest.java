package com.bravson.socialalert.test.util;

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
		 
		 int precision = GeoHashUtil.computeGeoHashPrecision(geoBox, 64);
		 assertThat(precision).isEqualTo(6);
		 
		 String geoHash = GeoHashUtil.computeGeoHash(geoBox.getCenterLat(), geoBox.getCenterLon(), 4);
		 assertThat(geoHash).isEqualTo("u0m9");
	 }

}
