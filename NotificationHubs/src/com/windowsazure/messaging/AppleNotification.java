//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.hc.core5.http.ContentType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * This class represents an Apple APNS notification.
 */
public class AppleNotification extends Notification {

    /**
     * Creates a new instance of the AppleNotification class.
     * @param body The JSON body for the notification.
     */
    public AppleNotification(String body) {
        this(body, getDefaultExpiration(), null);
    }

    /**
     * Creates a new instance of the AppleNotification class.
     * @param body The JSON body for the notification.
     * @param headers The headers for the notification.
     */
    public AppleNotification(String body, Map<String, String> headers) {
        this(body, getDefaultExpiration(), headers);
    }

    /**
     * Creates a new instance of the AppleNotification class.
     * @param body The JSON body for the notification.
     * @param expiry The expiration date of the notification.
     */
    public AppleNotification(String body, Date expiry) {
        this(body, expiry, null);
    }

    /**
     * Creates a new instance of the AppleNotification class.
     * @param body The JSON body for the notification.
     * @param expiry The expiration date of the notification.
     * @param headers The headers for the notification.
     */
    public AppleNotification(String body, Date expiry, Map<String, String> headers) {
        this.body = body;
        this.contentType = ContentType.APPLICATION_JSON;

        this.headers.put("ServiceBusNotification-Format", "apple");

        if (expiry != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String expiryString = formatter.format(expiry.getTime());

            this.headers.put("ServiceBusNotification-Apns-Expiry", expiryString);
        }

        if (headers != null) {
            this.headers = headers;
        }
    }

    private static Date getDefaultExpiration() {
        Date now = new Date();
        return new Date(now.getTime() + 24 * 60 * 60 * 1000);
    }
}
