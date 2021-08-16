package com.windowsazure.messaging;

import org.apache.http.entity.ContentType;

/**
 * This class represents a notification to the Amazon PNS.
 */
public class AdmNotification extends Notification {

    /**
     * Creates a new instance of the AdmNotification class.
     * @param body The JSON body for the Amazon PNS.
     */
    public AdmNotification(String body) {
        this.body = body;
        this.contentType = ContentType.APPLICATION_JSON;

        this.headers.put("ServiceBusNotification-Format", "adm");
    }
}
