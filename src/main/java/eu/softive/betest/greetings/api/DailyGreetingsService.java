package eu.softive.betest.greetings.api;

import java.time.LocalTime;
import java.util.Locale;

public interface DailyGreetingsService {
	public GreetingMessage greeting(Locale locale) throws UnsupportedLocaleException;
	
	public GreetingMessage greeting(Locale locale, LocalTime time) throws UnsupportedLocaleException;
}
