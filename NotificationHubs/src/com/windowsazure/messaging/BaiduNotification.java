package com.windowsazure.messaging;

import org.apache.http.entity.ContentType;

/**
 * This class represents a notification for the Baidu PNS.
 */
public class BaiduNotification extends Notification {

    /**
     * Creates a new instance of the BaiduNotification class.
     * @param body The JSON body for the Baidu notification.
     */
    public BaiduNotification(String body) {
        this.body = body;
        this.contentType = ContentType.APPLICATION_JSON;

        this.headers.put("ServiceBusNotification-Format", "baidu");
    }
}
