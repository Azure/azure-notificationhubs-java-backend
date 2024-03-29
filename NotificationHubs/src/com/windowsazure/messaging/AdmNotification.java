//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.hc.core5.http.ContentType;

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
