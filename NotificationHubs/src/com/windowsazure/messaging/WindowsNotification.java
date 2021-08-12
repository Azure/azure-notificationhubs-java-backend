package com.windowsazure.messaging;

import org.apache.http.entity.ContentType;

/**
 * This class represents a notification for WNS.
 */
public class WindowsNotification extends Notification {

    /**
     * Creates a new instance of the WindowsNotification class.
     * @param body The XML or raw body for WNS.
     */
    public WindowsNotification(String body) {
        this.headers.put("ServiceBusNotification-Format", "windows");

        if (body.matches("^<toast[\\s\\S]*>[\\s\\S]+</toast>$"))
            this.headers.put("X-WNS-Type", "wns/toast");
        if (body.matches("^<tile[\\s\\S]*>[\\s\\S]+</tile>$"))
            this.headers.put("X-WNS-Type", "wns/tile");
        if (body.matches("^<badge[\\s\\S]*>[\\s\\S]+</badge>$"))
            this.headers.put("X-WNS-Type", "wns/badge");

        if (body.startsWith("<")) {
            this.contentType = ContentType.APPLICATION_XML;
        }
    }
}
