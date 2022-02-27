package eu.softive.betest.greetings.api;

public class GreetingMessage {
	private String greeting;

	public GreetingMessage(String greeting) {
		super();
		this.greeting = greeting;
	}

	public String getGreeting() {
		return greeting;
	}
	
	@Override
	public String toString() {
		return "GreetingMessage [greeting=" + greeting + "]";
	}
}
