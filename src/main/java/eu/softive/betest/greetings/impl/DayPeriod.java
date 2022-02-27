package eu.softive.betest.greetings.impl;

import java.time.LocalTime;

public enum DayPeriod {
	MORNING("05:00"), AFTERNOON("12:00"), EVENING("17:00"), GENERAL("22:00");
	
	private final LocalTime time;
	
	DayPeriod(final String time) {
		this.time = LocalTime.parse(time);
	}
	
	public LocalTime getTime() {
		return time;
	};
}
