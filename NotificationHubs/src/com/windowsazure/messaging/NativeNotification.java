package com.windowsazure.messaging;

import org.apache.http.entity.ContentType;

import java.util.Map;

/**
 * This interface represents a native notification.
 */
public interface NativeNotification {
    /**
     * Gets the headers for the notification.
     * @return The headers for the notification.
     */
    Map<String, String> getHeaders();

    /**
     * Sets the headers for the notification.
     * @param headers The headers for the notification.
     */
    void setHeaders(Map<String, String> headers);

    /**
     * Gets the body for the notification.
     * @return The body for the notification.
     */
    String getBody();

    /**
     * Sets the body for the notification.
     * @param body The body for the notification.
     */
    void setBody(String body);

    /**
     * Gets the Content-Type for the notification.
     * @return The Content-Type for the notification.
     */
    ContentType getContentType();

    /**
     * Sets the Content-Type for the notification.
     * @param contentType The Content-Type for the notification.
     */
    void setContentType(ContentType contentType);
}
