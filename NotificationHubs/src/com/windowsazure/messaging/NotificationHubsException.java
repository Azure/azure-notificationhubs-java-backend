//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;

import java.io.StringWriter;
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

    private static boolean isTransientStatusCode(int httpStatusCode) {
        return httpStatusCode == 403 || httpStatusCode == 408 || httpStatusCode == 429 || httpStatusCode == 500
            || httpStatusCode == 503 || httpStatusCode == 504;
    }

    private static Optional<Duration> parseRetryAfter(HttpResponse response) {
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

    private static String getErrorString(HttpResponse response) {
        StringWriter writer = new StringWriter();
        try{
            IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
            String body = writer.toString();
            return "Error: " + response.getStatusLine() + " - " + body;
        } catch (Exception e) {
            throw new RuntimeException("Cannot get the HTTP response error string");
        }

    }

    /**
     /**
     * Creates a NotificationHubsException instance based upon the HTTP response and status code.
     * @param response The HTTP response.
     * @param httpStatusCode The HTTP status code from the response.
     * @return A new NotificationHubsException instance based upon the HTTP response data.
     */
    public static NotificationHubsException create(HttpResponse response, int httpStatusCode) {
        Optional<Duration> retryAfter = parseRetryAfter(response);
        boolean isTransient = isTransientStatusCode(httpStatusCode);
        if (retryAfter.isPresent()) {
            return new NotificationHubsException(getErrorString(response), httpStatusCode, isTransient,
                retryAfter.get());
        } else {
            return new NotificationHubsException(getErrorString(response), httpStatusCode, isTransient);
        }
    }

    /**
     * Creates a NotificationHubsException instance based upon the HTTP response and status code.
     * @param response The HTTP response.
     * @param httpStatusCode The HTTP status code from the response.
     * @param message A message for the NotificationHubsException instance.
     * @return A new NotificationHubsException instance based upon the HTTP response data.
     */
    public static NotificationHubsException create(
        HttpResponse response,
        int httpStatusCode,
        String message
    ) {
        Optional<Duration> retryAfter = parseRetryAfter(response);
        boolean isTransient = isTransientStatusCode(httpStatusCode);
        if (retryAfter.isPresent()) {
            return new NotificationHubsException(message, httpStatusCode, isTransient, retryAfter.get());
        } else {
            return new NotificationHubsException(message, httpStatusCode, isTransient);
        }
    }
}
