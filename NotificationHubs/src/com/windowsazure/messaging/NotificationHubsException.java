package com.windowsazure.messaging;

/**
 * This class represents an exception from the notification hubs client.
 */
@SuppressWarnings("serial")
public class NotificationHubsException extends Exception {
	private final int httpStatusCode;

	/**
	 * Creates a notification hub exception with message and HTTP status code.
	 * @param message The message for the exception.
	 * @param httpStatusCode The HTTP status code for the exception.
	 */
	public NotificationHubsException(String message, int httpStatusCode) {
		super(message);
		this.httpStatusCode = httpStatusCode;
	}

	/**
	 * Gets the HTTP status code for the exception.
	 * @return The HTTP status code for the exception.
	 */
	public int getHttpStatusCode() {
		return this.httpStatusCode;
	}
}
