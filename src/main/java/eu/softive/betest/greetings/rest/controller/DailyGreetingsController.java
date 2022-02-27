package eu.softive.betest.greetings.rest.controller;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import eu.softive.betest.greetings.api.DailyGreetingsService;
import eu.softive.betest.greetings.api.GreetingMessage;
import eu.softive.betest.greetings.api.UnsupportedLocaleException;

@RestController
@RequestMapping(path = "/daily")
public class DailyGreetingsController {
	@Autowired
	private DailyGreetingsService dailyGreetingsService;
	@Autowired
	private MessageSource messageSource;

	private static final Logger logger = LoggerFactory.getLogger(DailyGreetingsController.class);
	private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_TIME;
	
	@GetMapping("/greeting")
	public GreetingMessage greeting(@RequestParam(name = "locale", required = true) String localeCode,
			@RequestParam(name = "time", required = false) String timeString) {
		// process query params
		Locale locale = Locale.forLanguageTag(localeCode);
		LocalTime time = null;
		if (timeString != null) {
			try {
				time = LocalTime.parse(timeString, timeFormatter);
			} catch (DateTimeParseException e) {
				String message = messageSource.getMessage("error.parseTimeFailed", new Object[] { timeString },
						Locale.getDefault());
				logger.error(message);
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
			}
		}

		// get the greeting message
		GreetingMessage greetingMessage;
		try {
			if (time != null) {
				greetingMessage = dailyGreetingsService.greeting(locale, time);
			} else {
				greetingMessage = dailyGreetingsService.greeting(locale);
			}
		} catch (UnsupportedLocaleException e) {
			String message = messageSource.getMessage("error.localeNotSupported", new Object[] { localeCode },
					Locale.getDefault());
			logger.error(message);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message, e);
		
		}
		
		return greetingMessage;
	}
}
