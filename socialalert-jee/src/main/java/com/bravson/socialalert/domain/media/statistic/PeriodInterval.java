package com.bravson.socialalert.domain.media.statistic;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalAmount;

public enum PeriodInterval {
	HOUR {
		@Override
		public TemporalAmount toTemporalAmount() {
			return Duration.ofHours(1);
		}
	},
	DAY {
		@Override
		public TemporalAmount toTemporalAmount() {
			return Period.ofDays(1);
		}
	},
	WEEK {
		@Override
		public TemporalAmount toTemporalAmount() {
			return Period.ofWeeks(1);
		}
	},
	MONTH {
		@Override
		public TemporalAmount toTemporalAmount() {
			return Period.ofMonths(1);
		}
	},
	QUARTER {
		@Override
		public TemporalAmount toTemporalAmount() {
			return Period.ofMonths(3);
		}
	},
	YEAR {
		@Override
		public TemporalAmount toTemporalAmount() {
			return Period.ofYears(1);
		}
	};
	
	public abstract TemporalAmount toTemporalAmount();
}
