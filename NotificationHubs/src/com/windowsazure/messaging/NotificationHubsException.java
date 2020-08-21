//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.time.Duration;
import java.util.Optional;

/**
 * This class represents an exception from the notification hubs client.
 */
@SuppressWarnings("serial")
public class NotificationHubsException extends Exception {
	private final int httpStatusCode;
	private final boolean isTransient;
	private Duration retryAfter;

	/**
	 * Creates a notification hub exception with message and HTTP status code.
	 * @param message The message for the exception.
	 * @param httpStatusCode The HTTP status code for the exception.
	 */
	public NotificationHubsException(String message, int httpStatusCode, boolean isTransient) {
		super(message);
		this.httpStatusCode = httpStatusCode;
		this.isTransient = isTransient;
	}

	public NotificationHubsException(String message, int httpStatusCode, boolean isTransient, Duration retryAfter) {
        this(message, httpStatusCode, isTransient);
        this.retryAfter = retryAfter;
    }

	/**
	 * Gets the HTTP status code for the exception.
	 * @return The HTTP status code for the exception.
	 */
	public int getHttpStatusCode() {
		return this.httpStatusCode;
	}

	public boolean getIsTransient() {
	    return this.isTransient;
    }

    public Optional<Duration> getRetryAfter() {
	    return Optional.ofNullable(this.retryAfter);
    }
}
