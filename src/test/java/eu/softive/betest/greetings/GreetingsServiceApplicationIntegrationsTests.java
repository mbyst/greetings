package eu.softive.betest.greetings;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import eu.softive.betest.greetings.impl.PoliteDailyGreetings;

@SpringBootTest
@AutoConfigureMockMvc
class GreetingsServiceApplicationIntegrationsTests {
	private static final String DEILY_GREETING_ENDPOINT = "/daily/greeting";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private PoliteDailyGreetings dailyGreetingsService;
	
	@Test
	void contextLoads() {
	}

	@Test
	void shouldReturnGeneralGreeting() throws Exception {
		MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders
			.get(DEILY_GREETING_ENDPOINT)
			.param("locale", "en-GB")
			.accept(MediaType.APPLICATION_JSON_VALUE))
			.andReturn();
		
		// {"greeting":"Hello"}
		assertEquals(200, mvcResult.getResponse().getStatus());
	}
	
	@Test
	void shouldReturnTimeSpecificGreeting() throws Exception {
		MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders
				.get(DEILY_GREETING_ENDPOINT)
				.param("locale", "en-GB")
				.param("time", "10:10")
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		
		assertEquals(200, mvcResult.getResponse().getStatus());
	}
	
	@Test
	void shouldFailForInvalidTime() throws Exception {
		MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders
				.get(DEILY_GREETING_ENDPOINT)
				.param("locale", "en-GB")
				.param("time", "10,10")
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		
		assertEquals(400, mvcResult.getResponse().getStatus());
	}
	
	@Test
	void shouldFailForUnsupportedLocale() throws Exception {
		MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders
				.get(DEILY_GREETING_ENDPOINT)
				.param("locale", "de-DE")
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		
		assertEquals(400, mvcResult.getResponse().getStatus());
	}
}
