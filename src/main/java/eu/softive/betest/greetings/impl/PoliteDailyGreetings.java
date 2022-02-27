package eu.softive.betest.greetings.impl;

import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import eu.softive.betest.greetings.api.DailyGreetingsService;
import eu.softive.betest.greetings.api.GreetingMessage;
import eu.softive.betest.greetings.api.UnsupportedLocaleException;

@Service
public class PoliteDailyGreetings implements DailyGreetingsService {
	private static final Logger logger = LoggerFactory.getLogger(PoliteDailyGreetings.class);
	private static final String ENV_GREETINGS_BUNDLE = "greetingsBunde";

	private Map<Locale, Properties> greetingsMap = new HashMap<Locale, Properties>();

	public PoliteDailyGreetings() {
		String greetingsBundle = System.getenv(ENV_GREETINGS_BUNDLE);
		if (greetingsBundle == null) {
			greetingsBundle = "greetings";
		}

		Map<Locale, Properties> greetings;
		try {
			greetings = loadGreetingsLocales(greetingsBundle);
		} catch (IOException e) {
			throw new InitializationException("Failed to find greetings bundles", e);
		}

		if (greetings.isEmpty()) {
			throw new InitializationException("No greetings found for any locale.");
		} else {
			this.greetingsMap = greetings;
			logger.info("Greetings initialized for locales: {}", greetingsMap.keySet());
		}
	}

	@Override
	public GreetingMessage greeting(Locale locale) throws UnsupportedLocaleException {
		logger.info("Determining general greeting for locale={}", locale);
		Properties messages = getGreetingsForLocale(locale);

		String greeting = messages.getProperty("general");
		return new GreetingMessage(greeting);
	}

	@Override
	public GreetingMessage greeting(Locale locale, LocalTime time) throws UnsupportedLocaleException {
		logger.info("Determining time-specific greeting for locale={} and time={}", locale, time);
		Properties messages = getGreetingsForLocale(locale);
		
		// determine time-specific greeting based on pre-defined day periods
		String greeting = null;
		if (time.isBefore(DayPeriod.MORNING.getTime())) {
			greeting = messages.getProperty("general");
		} else if (time.isBefore(DayPeriod.AFTERNOON.getTime())) {
			greeting = messages.getProperty("morning");
		} else if (time.isBefore(DayPeriod.EVENING.getTime())) {
			greeting = messages.getProperty("afternoon");
		} else if (time.isBefore(DayPeriod.GENERAL.getTime())) {
			greeting = messages.getProperty("evening");
		}
		
		if (greeting == null) {
			greeting = messages.getProperty("general");
		}
		
		return new GreetingMessage(greeting);
	}

	/**
	 * Finds and load all greetings messages in a classpath with a given bundle
	 * name. The bundle are properties files with names consisting of bundleName
	 * (same for all locales) + IETF language tag concatenated withssss underscore
	 * 
	 * @param greetingsBundleName
	 * @return
	 * @throws IOException
	 */
	private Map<Locale, Properties> loadGreetingsLocales(String greetingsBundleName) throws IOException {
		Pattern pattern = Pattern.compile("^" + greetingsBundleName + "_(.*).properties$");
		Map<Locale, Properties> greetingsMap = new HashMap<Locale, Properties>();

		PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resourcePatternResolver
				.getResources("classpath:**/" + greetingsBundleName + "*.properties");
		for (Resource resource : resources) {
			String fileName = resource.getFilename();
			Matcher matcher = pattern.matcher(fileName);
			if (matcher.find()) {
				// parse locale tag out of file name
				String localeString = matcher.group(1);
				Locale locale = Locale.forLanguageTag(localeString);

				// load greetings messages
				Properties greetings = new Properties();
				greetings.load(resource.getInputStream());

				// check if messages contains general greeting for this locale
				String generalGreeting = greetings.getProperty("general");
				if (generalGreeting != null && !generalGreeting.isBlank()) {
					greetingsMap.put(locale, greetings);
					logger.info("Greetings for locale={} loaded from file {} ({} messages)", locale, fileName,
							greetings.size());
				} else {
					logger.warn(
							"Failed to load greetings for locale={}: general greeting is missing. The locale will be unsupported.",
							locale);
				}
			}
		}

		return greetingsMap;
	}

	private Properties getGreetingsForLocale(Locale locale) throws UnsupportedLocaleException {
		Properties messages = greetingsMap.get(locale);
		if (messages == null) {
			throw new UnsupportedLocaleException(locale.toLanguageTag());
		}

		return messages;
	}
}
