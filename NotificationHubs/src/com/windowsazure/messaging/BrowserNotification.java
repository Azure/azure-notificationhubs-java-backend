//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.hc.core5.http.ContentType;

/**
 * This class represents a notification to the browser PNS.
 */
public class BrowserNotification extends Notification {

    /**
     * Creates a new instance of the BrowserNotification class.
     * @param body The JSON body.
     */
    public BrowserNotification(String body) {
        this.body = body;
        this.contentType = ContentType.APPLICATION_JSON;
        this.headers.put("ServiceBusNotification-Format", "browser");
    }
}
