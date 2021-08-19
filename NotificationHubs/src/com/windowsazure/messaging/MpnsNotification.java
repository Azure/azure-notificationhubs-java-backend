//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.http.entity.ContentType;

/**
 * This class represents a Windows Phone notification.
 */
public class MpnsNotification extends Notification {

    /**
     * Creates a new instance of the MpnsNotification class.
     * @param body The XML body for the Windows Phone PNS.
     */
    public MpnsNotification(String body) {
        this.body = body;

        this.headers.put("ServiceBusNotification-Format", "windowsphone");

        if (body.contains("<wp:Toast>")) {
            this.headers.put("X-WindowsPhone-Target", "toast");
            this.headers.put("X-NotificationClass", "2");
        }
        if (body.contains("<wp:Tile>")) {
            this.headers.put("X-WindowsPhone-Target", "tile");
            this.headers.put("X-NotificationClass", "1");
        }

        if (body.startsWith("<")) {
            this.contentType = ContentType.APPLICATION_XML;
        }
    }
}
