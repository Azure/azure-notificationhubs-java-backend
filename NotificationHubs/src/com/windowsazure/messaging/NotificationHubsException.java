//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.message.StatusLine;

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
     *
     * @param message        The message for the exception.
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
     *
     * @return The HTTP status code for the exception.
     */
    public int httpStatusCode() {
        return httpStatusCode;
    }

    /**
     * Gets whether the HTTP status code is transient.
     *
     * @return True if transient and can be retried.
     */
    public boolean isTransient() {
        return isTransient;
    }

    /**
     * Gets the retry after duration, if specified.
     *
     * @return The duration for when the operation should be tried again.
     */
    public Optional<Duration> retryAfter() {
        return Optional.ofNullable(retryAfter);
    }

    public static boolean isTransientStatusCode(int httpStatusCode) {
        return httpStatusCode == 403 || httpStatusCode == 408 || httpStatusCode == 429 || httpStatusCode == 500
            || httpStatusCode == 503 || httpStatusCode == 504;
    }

    private static Optional<Duration> parseRetryAfter(SimpleHttpResponse response) {
        Header retryAfter = response.getFirstHeader(HttpHeaders.RETRY_AFTER);
        if (retryAfter == null) {
            return Optional.empty();
        }
        String retryAfterValue = retryAfter.getValue();
        if (retryAfterValue.equals("")) {
            return Optional.empty();
        }

        try {
            long retryAfterSeconds = Long.parseLong(retryAfterValue);
            return Optional.of(Duration.ofSeconds(retryAfterSeconds));
        } catch (NumberFormatException e) {
            throw new UnsupportedOperationException(
                String.format("\"%s\" must be an integer number of seconds", retryAfterValue));
        }
    }

    private static String getErrorString(SimpleHttpResponse response, String trackingId) {
        String msg = response.getBodyText() != null ? response.getBodyText() : "";
        StatusLine statusLine = new StatusLine(response);
        return String.format("Tracking ID: %s Error: %s - %s", trackingId, statusLine, msg);
    }

    /**
     /**
     * Creates a NotificationHubsException instance based upon the HTTP response and status code.
     * @param response The HTTP response.
     * @param httpStatusCode The HTTP status code from the response.
     * @param trackingId The Tracking ID
     * @return A new NotificationHubsException instance based upon the HTTP response data.
     */
    public static NotificationHubsException create(SimpleHttpResponse response, int httpStatusCode, String trackingId) {
        Optional<Duration> retryAfter = parseRetryAfter(response);
        boolean isTransient = isTransientStatusCode(httpStatusCode);
        if (retryAfter.isPresent()) {
            return new NotificationHubsException(getErrorString(response, trackingId), httpStatusCode, isTransient,
                retryAfter.get());
        } else {
            return new NotificationHubsException(getErrorString(response, trackingId), httpStatusCode, isTransient);
        }
    }
}
