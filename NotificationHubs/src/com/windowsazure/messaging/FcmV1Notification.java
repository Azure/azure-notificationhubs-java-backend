//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.hc.core5.http.ContentType;

/**
 * This class represents a notification to the Firebase Cloud Messaging V1 service.
 */
public class FcmV1Notification extends Notification {

    /**
     * Creates a new instance of the FcmV1Notification class.
     * @param body The JSON body for the Firebase Cloud Messaging V1 service.
     */
    public FcmV1Notification(String body) {
        this.body = body;
        this.contentType = ContentType.APPLICATION_JSON;
        this.headers.put("ServiceBusNotification-Format", "fcmv1");
    }
}
