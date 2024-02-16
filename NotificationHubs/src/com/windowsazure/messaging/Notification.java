//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.hc.core5.http.ContentType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        return new WindowsNotification(body);
    }

    /**
     * Utility method to set up a native notification for WNS. Sets the X-WNS-Type
     * header to "wns/raw" in order of sending of raw notification.
     *
     * @param body The body for the Windows Raw Notification
     * @return A native notification for WNS.
     */
    public static Notification createWindowsRawNotification(String body) {
        return new WindowsRawNotification(body);
    }

    /**
     * Utility method to set up a native notification for APNs. An expiry Date of 1
     * day is set by default.
     *
     * @param body the body for the Apple notification
     * @return A native notification for APNS.
     */
    public static Notification createAppleNotification(String body) {
        return new AppleNotification(body);
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
        return new AppleNotification(body, headers);
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
        return new AppleNotification(body, expiry);
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
    public static AppleNotification createAppleNotification(String body, Date expiry, Map<String, String> headers) {
        return new AppleNotification(body, expiry, headers);
    }

        /**
     * Utility method to set up a native notification for GCM.
     *
     * @param body the body for the GCM message.
     * @return a GCM notification with the body
     * @deprecated use {@link #createFcmV1Notification(String)} instead.
     */
    @Deprecated
    public static Notification createGcmNotification(String body) {
        return new FcmNotification(body);
    }

    /**
     * Utility method to set up a native notification for FCM.
     *
     * @param body the body for the FCM notification
     * @return an FCM notification
     * @deprecated use {@link #createFcmV1Notification(String)} instead.
     */
    @Deprecated
    public static Notification createFcmNotification(String body) {
        return new FcmNotification(body);
    }

    /**
     * Utility method to set up a native notification for FCM V1.
     *
     * @param body the body for the FCM V1 notification
     * @return an FCM V1 notification
     */
    public static Notification createFcmV1Notification(String body) {
        return new FcmV1Notification(body);
    }

    /**
     * Utility method to set up a native notification for ADM.
     *
     * @param body The body for the ADM notification.
     * @return an ADM notification with the given body.
     */
    public static Notification createAdmNotification(String body) {
        return new AdmNotification(body);
    }

    /**
     * Utility method to set up a native notification for Baidu PNS.
     *
     * @param body the body for the Baidu notification
     * @return a Baidu notification with the given body.
     */
    public static Notification createBaiduNotification(String body) {
        return new BaiduNotification(body);
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
        return new MpnsNotification(body);
    }

    /**
     * Utility method to create a notification object representing a template
     * notification.
     *
     * @param properties The properties for the template notification
     * @return a template notification with the associated properties.
     */
    public static Notification createTemplateNotification(Map<String, String> properties) {
        return new TemplateNotification(properties);
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
