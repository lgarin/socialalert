package com.bravson.socialalert.test.util;

import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import com.bravson.socialalert.infrastructure.util.GeoHashUtil;

import ch.hsr.geohash.BoundingBox;

public class GeoHashUtilTest extends Assertions {
	
	 @Test
	 public void computeBoundingBox() {
		 BoundingBox box = GeoHashUtil.computeBoundingBox("u0m9");
		 assertThat(box.getNorthLatitude()).isEqualTo(46.75, Offset.offset(0.01));
		 assertThat(box.getSouthLatitude()).isEqualTo(46.58, Offset.offset(0.01));
		 assertThat(box.getEastLongitude()).isEqualTo(8.08, Offset.offset(0.01));
		 assertThat(box.getWestLongitude()).isEqualTo(7.73, Offset.offset(0.01));
	 }

	 @Test
	 public void computeGeoHashPrecision() {
		 BoundingBox box = new BoundingBox(46.123042072708216, 46.89713427117885, 7.396524623036385, 8.526546992361546);
		 int precision = GeoHashUtil.computeGeoHashPrecision(box, 128);
		 assertThat(precision).isEqualTo(7);
	 }
}
