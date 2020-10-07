//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.http.entity.ContentType;

/**
 * Class representing a generic notification.
 */
public class Notification {

    Map<String, String> headers = new HashMap<>();
    String body;
    ContentType contentType;

    /**
     * Utility method to set up a native notification for WNS. Sets the X-WNS-Type
     * headers based on the body provided. If you want to send raw notifications you
     * have to set the X-WNS header and ContentType after creating this notification
     * or use createWindowsRawNotification method
     *
     * @param body The body for the notification
     * @return a new Windows Notification.
     */
    public static Notification createWindowsNotification(String body) {
        Notification n = new Notification();
        n.body = body;

        n.headers.put("ServiceBusNotification-Format", "windows");

        if (body.matches("^<toast[\\s\\S]*>[\\s\\S]+</toast>$"))
            n.headers.put("X-WNS-Type", "wns/toast");
        if (body.matches("^<tile[\\s\\S]*>[\\s\\S]+</tile>$"))
            n.headers.put("X-WNS-Type", "wns/tile");
        if (body.matches("^<badge[\\s\\S]*>[\\s\\S]+</badge>$"))
            n.headers.put("X-WNS-Type", "wns/badge");

        if (body.startsWith("<")) {
            n.contentType = ContentType.APPLICATION_XML;
        }

        return n;
    }

    /**
     * Utility method to set up a native notification for WNS. Sets the X-WNS-Type
     * header to "wns/raw" in order of sending of raw notification.
     *
     * @param body The body for the Windows Raw Notification
     * @return A native notification for WNS.
     */
    public static Notification createWindowsRawNotification(String body) {
        Notification n = new Notification();
        n.body = body;
        n.headers.put("ServiceBusNotification-Format", "windows");
        n.headers.put("X-WNS-Type", "wns/raw");
        n.contentType = ContentType.APPLICATION_OCTET_STREAM;
        return n;
    }

    /**
     * @param body the body for the Apple notification
     * @return A native notification for APNS.
     * @deprecated Typo in name, use createAppleNotification instead.
     * Utility method to set up a native notification for APNs. An expiry Date of 1
     * day is set by default.
     */
    @Deprecated
    public static Notification createAppleNotifiation(String body) {
        return createAppleNotification(body);
    }

    /**
     * Utility method to set up a native notification for APNs. An expiry Date of 1
     * day is set by default.
     *
     * @param body the body for the Apple notification
     * @return A native notification for APNS.
     */
    public static Notification createAppleNotification(String body) {
        Date now = new Date();
        Date tomorrow = new Date(now.getTime() + 24 * 60 * 60 * 1000);
        return createAppleNotification(body, tomorrow);
    }

    /**
     * Utility method to set up a native notification for APNs. Allows setting the
     * APNS Headers - as per the new APNs protocol.
     *
     * @param body    the body for the APNS message
     * @param headers the APNS headers
     * @return a native APNS notification
     */
    public static Notification createAppleNotification(String body, Map<String, String> headers) {
        Date now = new Date();
        Date tomorrow = new Date(now.getTime() + 24 * 60 * 60 * 1000);
        return createAppleNotification(body, tomorrow, headers);
    }

    /**
     * Utility method to set up a native notification for APNs. Enables to set the
     * expiry date of the notification for the APNs QoS.
     *
     * @param body   the body for the APNS notification.
     * @param expiry the expiration date of this notification. A null value will be
     *               interpreted as 0 seconds.
     * @return an APNS notification with expiration time.
     */
    public static Notification createAppleNotification(String body, Date expiry) {
        return createAppleNotification(body, expiry, null);
    }

    /**
     * Utility method to set up a native notification for APNs. Enables to set the
     * expiry date of the notification for the APNs QoS.
     *
     * @param body    the body for the APNS notification.
     * @param expiry  the expiration date of this notification. A null value will be
     *                interpreted as 0 seconds.
     * @param headers the APNS headers
     * @return an APNS notification with expiration time and headers.
     */
    public static Notification createAppleNotification(String body, Date expiry, Map<String, String> headers) {
        Notification n = new Notification();
        n.body = body;
        n.contentType = ContentType.APPLICATION_JSON;

        n.headers.put("ServiceBusNotification-Format", "apple");

        if (expiry != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String expiryString = formatter.format(expiry.getTime());

            n.headers.put("ServiceBusNotification-Apns-Expiry", expiryString);
        }

        if (headers != null) {
            n.headers = headers;
        }

        return n;
    }

    /**
     * Utility method to set up a native notification for GCM.
     *
     * @param body the body for the GCM message.
     * @return a GCM notification with the body
     * @deprecated use {@link #createFcmNotification(String)} instead.
     */
    @Deprecated
    public static Notification createGcmNotifiation(String body) {
        return createGcmNotification(body);
    }

    /**
     * Utility method to set up a native notification for GCM.
     *
     * @param body the body for the GCM message.
     * @return a GCM notification with the body
     * @deprecated use {@link #createFcmNotification(String)} instead.
     */
    @Deprecated
    public static Notification createGcmNotification(String body) {
        Notification n = new Notification();
        n.body = body;
        n.contentType = ContentType.APPLICATION_JSON;

        n.headers.put("ServiceBusNotification-Format", "gcm");

        return n;
    }

    /**
     * Utility method to set up a native notification for FCM.
     *
     * @param body the body for the FCM message.
     * @return a FCM notification with the body
     * @deprecated use {@link #createFcmNotification(String)} instead.
     */
    @Deprecated
    public static Notification createFcmNotifiation(String body) {
        return createFcmNotification(body);
    }

    /**
     * Utility method to set up a native notification for FCM.
     *
     * @param body the body for the FCM notification
     * @return an FCM notification
     */
    public static Notification createFcmNotification(String body) {
        Notification n = new Notification();
        n.body = body;
        n.contentType = ContentType.APPLICATION_JSON;
        n.headers.put("ServiceBusNotification-Format", "gcm");
        return n;
    }

    /**
     * Utility method to set up a native notification for ADM.
     *
     * @param body The body for the ADM notification.
     * @return an ADM notification with the given body.
     * @deprecated use {@link #createAdmNotification(String)} instead.
     */
    @Deprecated
    public static Notification createAdmNotifiation(String body) {
        return createAdmNotification(body);
    }

    /**
     * Utility method to set up a native notification for ADM.
     *
     * @param body The body for the ADM notification.
     * @return an ADM notification with the given body.
     */
    public static Notification createAdmNotification(String body) {
        Notification n = new Notification();
        n.body = body;
        n.contentType = ContentType.APPLICATION_JSON;

        n.headers.put("ServiceBusNotification-Format", "adm");

        return n;
    }

    /**
     * Utility method to set up a native notification for Baidu PNS.
     *
     * @param body the body for the Baidu notification
     * @return a Baidu notification with the given body.
     * @deprecated use {@link #createBaiduNotifiation(String)} instead.
     */
    @Deprecated
    public static Notification createBaiduNotifiation(String body) {
        return createBaiduNotification(body);
    }

    /**
     * Utility method to set up a native notification for Baidu PNS.
     *
     * @param body the body for the Baidu notification
     * @return a Baidu notification with the given body.
     */
    public static Notification createBaiduNotification(String body) {
        Notification n = new Notification();
        n.body = body;
        n.contentType = ContentType.APPLICATION_JSON;

        n.headers.put("ServiceBusNotification-Format", "baidu");

        return n;
    }

    /**
     * Utility method to set up a native notification for MPNS. Sets the
     * X-WindowsPhone-Target and X-NotificationClass headers based on the body
     * provided. Raw notifications are not supported for MPNS.
     *
     * @param body the body for the MPNS notification.
     * @return an initialized MPNS notification
     * @deprecated use {@link #createMpnsNotification(String)} instead.
     */
    @Deprecated
    public static Notification createMpnsNotifiation(String body) {
        return createMpnsNotification(body);
    }

    /**
     * Utility method to set up a native notification for MPNS. Sets the
     * X-WindowsPhone-Target and X-NotificationClass headers based on the body
     * provided. Raw notifications are not supported for MPNS.
     *
     * @param body the body for the MPNS notification.
     * @return an initialized MPNS notification
     */
    public static Notification createMpnsNotification(String body) {
        Notification n = new Notification();
        n.body = body;

        n.headers.put("ServiceBusNotification-Format", "windowsphone");

        if (body.contains("<wp:Toast>")) {
            n.headers.put("X-WindowsPhone-Target", "toast");
            n.headers.put("X-NotificationClass", "2");
        }
        if (body.contains("<wp:Tile>")) {
            n.headers.put("X-WindowsPhone-Target", "tile");
            n.headers.put("X-NotificationClass", "1");
        }

        if (body.startsWith("<")) {
            n.contentType = ContentType.APPLICATION_XML;
        }

        return n;
    }

    /**
     * Utility method to create a notification object representing a template
     * notification.
     *
     * @param properties The properties for the template notification
     * @return a template notification with the associated properties.
     */
    public static Notification createTemplateNotification(Map<String, String> properties) {
        Notification n = new Notification();
        StringBuilder buf = new StringBuilder();
        buf.append("{");
        for (Iterator<String> iterator = properties.keySet().iterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            buf.append("\"").append(key).append("\":\"").append(properties.get(key)).append("\"");
            if (iterator.hasNext())
                buf.append(",");
        }
        buf.append("}");
        n.body = buf.toString();

        n.contentType = ContentType.APPLICATION_JSON;

        n.headers.put("ServiceBusNotification-Format", "template");

        return n;
    }

    /**
     * Gets the headers for the notification.
     *
     * @return The headers for the notification.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Sets the headers for the notification.
     *
     * @param headers The headers for the notification.
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Gets the body for the notification.
     *
     * @return The body for the notification.
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the body for the notification.
     *
     * @param body The body for the notification.
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Gets the Content-Type for the notification.
     *
     * @return The Content-Type for the notification.
     */
    public ContentType getContentType() {
        return contentType;
    }

    /**
     * Sets the Content-Type for the notification.
     *
     * @param contentType The Content-Type for the notification.
     */
    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }
}
