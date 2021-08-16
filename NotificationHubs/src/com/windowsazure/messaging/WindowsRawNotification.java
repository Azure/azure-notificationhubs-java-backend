package com.windowsazure.messaging;

import org.apache.http.entity.ContentType;

/**
 * This class represents a raw notification for WNS.
 */
public class WindowsRawNotification extends Notification {

    /**
     * Creates a new instance of the WindowsRawNotification class.
     * @param body The raw body to send to WNS.
     */
    public WindowsRawNotification(String body) {
        this.body = body;
        this.headers.put("ServiceBusNotification-Format", "windows");
        this.headers.put("X-WNS-Type", "wns/raw");
        this.contentType = ContentType.APPLICATION_OCTET_STREAM;
    }
}
