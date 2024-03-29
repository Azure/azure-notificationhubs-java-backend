//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.hc.core5.http.ContentType;

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
