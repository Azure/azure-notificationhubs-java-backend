//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

/**
 * This class represents the notification telemetry.
 */
public class NotificationTelemetry {

    private String notificationId;
    private String location;
    private NotificationStatus notificationStatus;
    private Date enqueueTime;
    private Date startTime;
    private Date endTime;
    private String notificationBody;
    private String targetPlatforms;
    private Map<String, Integer> apnsOutcomeCounts;
    private Map<String, Integer> mpnsOutcomeCounts;
    private Map<String, Integer> wnsOutcomeCounts;
    private Map<String, Integer> gcmOutcomeCounts;
    private Map<String, Integer> fcmOutcomeCounts;
    private Map<String, Integer> admOutcomeCounts;
    private Map<String, Integer> baiduOutcomeCounts;
    private String pnsErrorDetailsUri;

    private static final ThreadLocal<Digester> parser;

    static {
        parser = new ThreadLocal<Digester>() {
            @Override
            protected Digester initialValue() {
                Digester instance = new Digester();
                setupParser(instance);
                return instance;
            }
        };
    }

    /**
     * Parses the notification telemetry from the incoming stream.
     *
     * @param content The stream to parse the notification telemetry.
     * @return A parsed notification telemetry from the stream.
     * @throws IOException  If there was an issue reading the stream.
     * @throws SAXException If there was an issue parsing the XML.
     */
    public static NotificationTelemetry parseOne(InputStream content) throws IOException, SAXException {
        return parser.get().parse(content);
    }

    /**
     * Gets the notification ID.
     *
     * @return The notification ID.
     */
    public String getNotificationId() {
        return notificationId;
    }

    /**
     * Sets the notification ID.
     *
     * @param notificationId The notification ID.
     */
    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    /**
     * Gets the location for the notification.
     *
     * @return Sets the location for the notification.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location for the notification.
     *
     * @param location The location for the notification.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the notification status
     *
     * @return The notification status.
     */
    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    /**
     * Sets the notification status from a string to be parsed as a
     * NotificationStatus.
     *
     * @param status A string to be parsed as a NotificationStatus.
     */
    public void setNotificationStatusFromString(String status) {
        this.notificationStatus = Enum.valueOf(NotificationStatus.class, status);
    }

    /**
     * Sets the notification status.
     *
     * @param notificationStatus The notification status to set.
     */
    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    /**
     * Gets the enqueue time for the notification.
     *
     * @return The notification enqueue time.
     */
    public Date getEnqueueTime() {
        return enqueueTime;
    }

    /**
     * Sets the enqueue date from a string to be parsed as a date.
     *
     * @param enqueueTime The enqueue date to be parsed as a date.
     */
    public void setEnqueueTimeFromString(String enqueueTime) {
        this.enqueueTime = javax.xml.bind.DatatypeConverter.parseDateTime(enqueueTime).getTime();
    }

    /**
     * Sets the enqueue time for the notification.
     *
     * @param enqueueTime The enqueue time for the notification.
     */
    public void setEnqueueTime(Date enqueueTime) {
        this.enqueueTime = enqueueTime;
    }

    /**
     * Gets the notification start time.
     *
     * @return The notification start time.
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Sets the notification start time from the string to be parsed as a date.
     *
     * @param startTime The notification start time as a string to be parsed as a
     *                  date/
     */
    public void setStartTimeFromString(String startTime) {
        this.startTime = javax.xml.bind.DatatypeConverter.parseDateTime(startTime).getTime();
    }

    /**
     * Sets the start time for the notification.
     *
     * @param startTime The start time for the notification.
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the end time for the notification.
     *
     * @return The end time for the notification.
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * Sets the notification end time from a string to be parsed as a string.
     *
     * @param endTime The notification end time as a string to be parsed as a date.
     */
    public void setEndTimeFromString(String endTime) {
        this.endTime = javax.xml.bind.DatatypeConverter.parseDateTime(endTime).getTime();
    }

    /**
     * Sets the notification end time.
     *
     * @param endTime The notification end time.
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * Gets the notification body.
     *
     * @return The notification body.
     */
    public String getNotificationBody() {
        return notificationBody;
    }

    /**
     * Sets the notification body.
     *
     * @param notificationBody The notification body.
     */
    public void setNotificationBody(String notificationBody) {
        this.notificationBody = notificationBody;
    }

    /**
     * Gets the target platforms for the notification.
     *
     * @return The target platforms for the notification.
     */
    public String getTargetPlatforms() {
        return targetPlatforms;
    }

    /**
     * Sets the target platforms.
     *
     * @param targetPlatforms The target platforms for the notification.
     */
    public void setTargetPlatforms(String targetPlatforms) {
        this.targetPlatforms = targetPlatforms;
    }

    /**
     * Gets the APNS outcome counts.
     *
     * @return The APNS outcome counts.
     */
    public Map<String, Integer> getApnsOutcomeCounts() {
        return apnsOutcomeCounts;
    }

    /**
     * Sets the APNS outcome counts.
     *
     * @param apnsOutcomeCounts The APNS outcome counts.
     */
    public void setApnsOutcomeCounts(Map<String, Integer> apnsOutcomeCounts) {
        this.apnsOutcomeCounts = apnsOutcomeCounts;
    }

    /**
     * Gets the Windows Phone MPNS outcome counts.
     *
     * @return The MPNS Windows phone outcome counts.
     */
    public Map<String, Integer> getMpnsOutcomeCounts() {
        return mpnsOutcomeCounts;
    }

    /**
     * Sets the MPNS Windows Phone outcome counts.
     *
     * @param mpnsOutcomeCounts The Windows Phone MPNS outcome counts.
     */
    public void setMpnsOutcomeCounts(Map<String, Integer> mpnsOutcomeCounts) {
        this.mpnsOutcomeCounts = mpnsOutcomeCounts;
    }

    /**
     * Gets the WNS outcome counts.
     *
     * @return The WNS outcome counts.
     */
    public Map<String, Integer> getWnsOutcomeCounts() {
        return wnsOutcomeCounts;
    }

    /**
     * Sets the WNS outcome counts.
     *
     * @param wnsOutcomeCounts The WNS outcome counts.
     */
    public void setWnsOutcomeCounts(Map<String, Integer> wnsOutcomeCounts) {
        this.wnsOutcomeCounts = wnsOutcomeCounts;
    }

    /**
     * Gets the GCM outcome counts.
     *
     * @return The GCM outcome counts;
     */
    public Map<String, Integer> getGcmOutcomeCounts() {
        return gcmOutcomeCounts;
    }

    /**
     * Sets the GCM outcome counts.
     *
     * @param gcmOutcomeCounts The GCM outcome counts.
     */
    public void setGcmOutcomeCounts(Map<String, Integer> gcmOutcomeCounts) {
        this.gcmOutcomeCounts = gcmOutcomeCounts;
    }

    /**
     * Gets the FCM outcome counts.
     *
     * @return The FCM outcome counts.
     */
    public Map<String, Integer> getFcmOutcomeCounts() {
        return fcmOutcomeCounts;
    }

    /**
     * Sets the FCM outcome counts.
     *
     * @param fcmOutcomeCounts The FCM outcome counts.
     */
    public void setFcmOutcomeCounts(Map<String, Integer> fcmOutcomeCounts) {
        this.fcmOutcomeCounts = fcmOutcomeCounts;
    }

    /**
     * Gets the Baidu outcome counts.
     *
     * @return The Baidu outcome counts.
     */
    public Map<String, Integer> getBaiduOutcomeCounts() {
        return baiduOutcomeCounts;
    }

    /**
     * Sets the Baidu outcome counts.
     *
     * @param baiduOutcomeCounts The Baidu outcome counts.
     */
    public void setBaiduOutcomeCounts(Map<String, Integer> baiduOutcomeCounts) {
        this.baiduOutcomeCounts = baiduOutcomeCounts;
    }

    /**
     * Gets the ADM outcome counts.
     *
     * @return The ADM outcome counts.
     */
    public Map<String, Integer> getAdmOutcomeCounts() {
        return admOutcomeCounts;
    }

    /**
     * Sets the ADM outcome counts.
     *
     * @param admOutcomeCounts The ADM outcome counts.
     */
    public void setAdmOutcomeCounts(Map<String, Integer> admOutcomeCounts) {
        this.admOutcomeCounts = admOutcomeCounts;
    }

    /**
     * Gets the URI for the PNS error details blob.
     *
     * @return The URI for the PNS error details blob.
     */
    public String getPnsErrorDetailsUri() {
        return pnsErrorDetailsUri;
    }

    /**
     * Sets the URI for the PNS error details blob.
     *
     * @param pnsErrorDetailsUri The URI for the PNS error details blob.
     */
    public void setPnsErrorDetailsUri(String pnsErrorDetailsUri) {
        this.pnsErrorDetailsUri = pnsErrorDetailsUri;
    }

    private static void setupParser(Digester digester) {
        digester.addObjectCreate("*/NotificationDetails", NotificationTelemetry.class);
        digester.addCallMethod("*/NotificationId", "setNotificationId", 1);
        digester.addCallParam("*/NotificationId", 0);
        digester.addCallMethod("*/Location", "setLocation", 1);
        digester.addCallParam("*/Location", 0);
        digester.addCallMethod("*/State", "setNotificationStatusFromString", 1);
        digester.addCallParam("*/State", 0);
        digester.addCallMethod("*/EnqueueTime", "setEnqueueTimeFromString", 1);
        digester.addCallParam("*/EnqueueTime", 0);
        digester.addCallMethod("*/StartTime", "setStartTimeFromString", 1);
        digester.addCallParam("*/StartTime", 0);
        digester.addCallMethod("*/EndTime", "setEndTimeFromString", 1);
        digester.addCallParam("*/EndTime", 0);
        digester.addCallMethod("*/NotificationBody", "setNotificationBody", 1);
        digester.addCallParam("*/NotificationBody", 0);
        digester.addCallMethod("*/TargetPlatforms", "setTargetPlatforms", 1);
        digester.addCallParam("*/TargetPlatforms", 0);
        digester.addCallMethod("*/PnsErrorDetailsUri", "setPnsErrorDetailsUri", 1);
        digester.addCallParam("*/PnsErrorDetailsUri", 0);

        digester.addObjectCreate("*/ApnsOutcomeCounts", HashMap.class);
        digester.addCallMethod("*/Outcome", "put", 2, new Class[] { String.class, Integer.class });
        digester.addCallParam("*/Name", 0);
        digester.addCallParam("*/Count", 1);
        digester.addSetNext("*/ApnsOutcomeCounts", "setApnsOutcomeCounts", Map.class.getName());

        digester.addObjectCreate("*/MpnsOutcomeCounts", HashMap.class);
        digester.addCallMethod("*/Outcome", "put", 2, new Class[] { String.class, Integer.class });
        digester.addCallParam("*/Name", 0);
        digester.addCallParam("*/Count", 1);
        digester.addSetNext("*/MpnsOutcomeCounts", "setMpnsOutcomeCounts", Map.class.getName());

        digester.addObjectCreate("*/WnsOutcomeCounts", HashMap.class);
        digester.addCallMethod("*/Outcome", "put", 2, new Class[] { String.class, Integer.class });
        digester.addCallParam("*/Name", 0);
        digester.addCallParam("*/Count", 1);
        digester.addSetNext("*/WnsOutcomeCounts", "setWnsOutcomeCounts", Map.class.getName());

        digester.addObjectCreate("*/GcmOutcomeCounts", HashMap.class);
        digester.addCallMethod("*/Outcome", "put", 2, new Class[] { String.class, Integer.class });
        digester.addCallParam("*/Name", 0);
        digester.addCallParam("*/Count", 1);
        digester.addSetNext("*/GcmOutcomeCounts", "setGcmOutcomeCounts", Map.class.getName());

        digester.addObjectCreate("*/FcmOutcomeCounts", HashMap.class);
        digester.addCallMethod("*/Outcome", "put", 2, new Class[] { String.class, Integer.class });
        digester.addCallParam("*/Name", 0);
        digester.addCallParam("*/Count", 1);
        digester.addSetNext("*/FcmOutcomeCounts", "setFcmOutcomeCounts", Map.class.getName());

        digester.addObjectCreate("*/AdmOutcomeCounts", HashMap.class);
        digester.addCallMethod("*/Outcome", "put", 2, new Class[] { String.class, Integer.class });
        digester.addCallParam("*/Name", 0);
        digester.addCallParam("*/Count", 1);
        digester.addSetNext("*/AdmOutcomeCounts", "setAdmOutcomeCounts", Map.class.getName());

        digester.addObjectCreate("*/BaiduOutcomeCounts", HashMap.class);
        digester.addCallMethod("*/Outcome", "put", 2, new Class[] { String.class, Integer.class });
        digester.addCallParam("*/Name", 0);
        digester.addCallParam("*/Count", 1);
        digester.addSetNext("*/BaiduOutcomeCounts", "setBaiduOutcomeCounts", Map.class.getName());
    }
}
