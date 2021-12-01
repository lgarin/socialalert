package com.bravson.socialalert.domain.histogram;

import java.time.Instant;

public interface PeriodCount {

	long getCount();
	
	void setCount(long count);
	
	Instant getPeriod();
}
