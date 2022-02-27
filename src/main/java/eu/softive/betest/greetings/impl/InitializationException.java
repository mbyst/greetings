package eu.softive.betest.greetings.impl;

public class InitializationException extends RuntimeException {
	private static final long serialVersionUID = -9046152065685910939L;

	public InitializationException(String message) {
		super(message);
	}
	
	public InitializationException(String message, Throwable cause) {
		super(message, cause);
	}
}
