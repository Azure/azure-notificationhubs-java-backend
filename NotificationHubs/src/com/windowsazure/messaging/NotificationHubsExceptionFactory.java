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
 * This class creates NotificationHubException classes based upon the HTTP response.
 */
public class NotificationHubsExceptionFactory {

    static boolean isTransientStatusCode(int httpStatusCode) {
        return httpStatusCode == 403 || httpStatusCode == 408 || httpStatusCode == 429 || httpStatusCode == 500
            || httpStatusCode == 503 || httpStatusCode == 504;
    }

    static Optional<Duration> parseRetryAfter(HttpResponse response) {
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

    static String getErrorString(HttpResponse response) {
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
    public static NotificationHubsException createNotificationHubException(HttpResponse response, int httpStatusCode) {
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
    public static NotificationHubsException createNotificationHubException(
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
