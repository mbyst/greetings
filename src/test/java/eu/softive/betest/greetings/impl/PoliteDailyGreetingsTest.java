package eu.softive.betest.greetings.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import eu.softive.betest.greetings.api.DailyGreetingsService;
import eu.softive.betest.greetings.api.GreetingMessage;
import eu.softive.betest.greetings.api.UnsupportedLocaleException;

class PoliteDailyGreetingsTest {
	private static DailyGreetingsService greetingsService;
	
	@BeforeAll
	static void init() {
		greetingsService = new PoliteDailyGreetings();
		
		assertNotNull(greetingsService);
	}
	
	@Test
	void shouldFailForUnsupportedLocale() {
		assertThrows(UnsupportedLocaleException.class, () -> {
			@SuppressWarnings("unused")
			GreetingMessage greeting = greetingsService.greeting(Locale.GERMAN);
		});
	}
	
	@Test
	void shouldReturnGeneralGreeting() throws UnsupportedLocaleException {
		GreetingMessage greeting = greetingsService.greeting(Locale.UK);
		
		assertNotNull(greeting);
		assertEquals(greeting.getGreeting(), "Hello");
	}
	
	@Test
	void shouldReturnMorningGreeting() throws UnsupportedLocaleException {
		GreetingMessage greeting = greetingsService.greeting(Locale.UK, LocalTime.parse("07:00"));
		
		assertNotNull(greeting);
		assertEquals(greeting.getGreeting(), "Good morning");
	}
	
	@Test
	void shouldReturnAfternoonGreeting() throws UnsupportedLocaleException {
		GreetingMessage greeting = greetingsService.greeting(Locale.UK, LocalTime.parse("12:00"));
		
		assertNotNull(greeting);
		assertEquals(greeting.getGreeting(), "Good afternoon");
	}
	
	@Test
	void shouldReturnEveningGreeting() throws UnsupportedLocaleException {
		GreetingMessage greeting = greetingsService.greeting(Locale.UK, LocalTime.parse("18:22"));
		
		assertNotNull(greeting);
		assertEquals(greeting.getGreeting(), "Good evening");
	}
	
	@Test
	void shouldReturnNightGreeting() throws UnsupportedLocaleException {
		GreetingMessage greeting = greetingsService.greeting(Locale.UK, LocalTime.parse("01:01:00"));
		
		assertNotNull(greeting);
		assertEquals(greeting.getGreeting(), "Hello");
	}
	
	@Test
	void shouldFallbackToGeneralGreeting() throws UnsupportedLocaleException {
		GreetingMessage greeting = greetingsService.greeting(Locale.US, LocalTime.parse("13:00"));
		
		assertNotNull(greeting);
		assertEquals(greeting.getGreeting(), "Hello");
	}
}
