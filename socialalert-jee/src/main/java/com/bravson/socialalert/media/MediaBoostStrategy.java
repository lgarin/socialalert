package com.bravson.socialalert.media;

import org.hibernate.search.engine.BoostStrategy;

public class MediaBoostStrategy implements BoostStrategy {

	@Override
	public float defineBoost(Object value) {
		if (value instanceof MediaEntity) {
			return (float) ((MediaEntity) value).getStatistic().computeBoost();
		}
		return 0.0f;
	}
}
