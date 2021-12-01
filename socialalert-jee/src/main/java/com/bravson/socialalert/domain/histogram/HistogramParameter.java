package com.bravson.socialalert.domain.histogram;

import java.util.Comparator;
import java.util.List;

import lombok.NonNull;
import lombok.Value;

@Value
public class HistogramParameter {

	@NonNull
	private PeriodInterval interval;
	private int maxSize;
	private boolean cumulation;
	
	public <T extends PeriodCount> List<T> filter(@NonNull List<T> inputList) {
		inputList.sort(Comparator.comparing(PeriodCount::getPeriod));
		
		if (cumulation) {
			long current = 0;
			for (T item : inputList) {
				current += item.getCount();
				item.setCount(current);
			}
		}
		if (maxSize < inputList.size()) {
			return inputList.subList(inputList.size() - maxSize, inputList.size());
		}
		return inputList;
	}
}
