//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.http.entity.ContentType;

/**
 * This class represents a notification to the Firebase Cloud Messaging service.
 */
public class FcmNotification extends Notification {

    /**
     * Creates a new instance of the FcmNotification class.
     * @param body The JSON body for the Firebase Cloud Messaging service.
     */
    public FcmNotification(String body) {
        this.body = body;
        this.contentType = ContentType.APPLICATION_JSON;
        this.headers.put("ServiceBusNotification-Format", "gcm");
    }
}
