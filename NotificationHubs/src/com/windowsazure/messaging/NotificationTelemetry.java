//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

/**
 * This class represents Azure Notification Hubs telemetry.
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
    private Map<String, Integer> fcmV1OutcomeCounts;
    private Map<String, Integer> admOutcomeCounts;
    private Map<String, Integer> baiduOutcomeCounts;
    private Map<String, Integer> browserOutcomeCounts;
    private String pnsErrorDetailsUri;

    private static final ThreadLocal<Digester> parser;

    static {
        parser = ThreadLocal.withInitial(() -> {
            Digester instance = new Digester();
            setupParser(instance);
            return instance;
        });
    }

    public static NotificationTelemetry parseOne(InputStream inputStream) throws IOException, SAXException {
        return parser.get().parse(inputStream);
    }

    public static NotificationTelemetry parseOne(byte[] bodyBytes) throws IOException, SAXException {
        return parser.get().parse(new ByteArrayInputStream(bodyBytes));
    }

    /**
     * Gets the Azure Notification Hubs notification ID.
     * @return The Azure Notification Hubs notification ID.
     */
    public String getNotificationId() { return notificationId; }

    /**
     * Sets the Azure Notification Hubs notification ID.
     * @param value The Azure Notification Hubs notification ID value to set.
     */
    public void setNotificationId(String value) { notificationId = value; }

    /**
     * Gets the location of the Azure Notification Hubs telemetry.
     * @return The location of the Azure Notification Hubs telemetry.
     */
    public String getLocation() { return location; }

    /**
     * Sets the location of the Azure Notification Hubs telemetry.
     * @param value The location of the Azure Notification Hubs telemetry value to set.
     */
    public void setLocation(String value) { location = value; }

    /**
     * Gets the status of the Azure Notification Hub notification.
     * @return The status of the Azure Notification Hub notification.
     */
    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    /**
     * Sets the status of the Azure Notification Hub notification.
     * @param value The status of the Azure Notification Hub notification value to set.
     */
    public void setNotificationStatus(NotificationStatus value) {
        notificationStatus = value;
    }

    /**
     * Sets the status of the Azure Notification Hub notification from a string value
     * @param value The status of the Azure Notification Hub notification string value to set.
     */
    public void setNotificationStatusFromString(String value) { notificationStatus = Enum.valueOf(NotificationStatus.class, value); }

    /**
     * Gets the enqueue time of the Azure Notification Hub notification.
     * @return The enqueue time of the Azure Notification Hub notification.
     */
    public Date getEnqueueTime() { return enqueueTime; }

    /**
     * Sets the enqueue time of the Azure Notification Hub notification.
     * @param value The enqueue time of the Azure Notification Hub notification value to set.
     */
    public void setEnqueueTime(Date value) { enqueueTime = value; }

    /**
     * Sets the enqueue time of the Azure Notification Hub notification from a string value
     * @param value The enqueue time of the Azure Notification Hub notification string value to set.
     */
    public void setEnqueueTimeFromString(String value) { enqueueTime = javax.xml.bind.DatatypeConverter.parseDateTime(value).getTime(); }

    /**
     * Gets the start time of the Azure Notification Hub notification.
     * @return The start time of the Azure Notification Hub notification.
     */
    public Date getStartTime() { return startTime; }

    /**
     * Sets the start time of the Azure Notification Hub notification.
     * @param value The start time of the Azure Notification Hub notification value to set.
     */
    public void setStartTime(Date value) { startTime = value; }

    /**
     * Sets the start time of the Azure Notification Hub notification from a string value
     * @param value The start time of the Azure Notification Hub notification string value to set.
     */
    public void setStartTimeFromString(String value) { startTime = javax.xml.bind.DatatypeConverter.parseDateTime(value).getTime(); }

    /**
     * Gets the end time of the Azure Notification Hub notification.
     * @return The end time of the Azure Notification Hub notification.
     */
    public Date getEndTime() { return endTime; }

    /**
     * Sets the end time of the Azure Notification Hub notification.
     * @param value The end time of the Azure Notification Hub notification value to set.
     */
    public void setEndTime(Date value) { endTime = value; }

    /**
     * Sets the end time of the Azure Notification Hub notification from a string value
     * @param value The end time of the Azure Notification Hub notification string value to set.
     */
    public void setEndTimeFromString(String value) { endTime = javax.xml.bind.DatatypeConverter.parseDateTime(value).getTime(); }

    /**
     * Gets the body of the Azure Notification Hub notification.
     * @return The body of the Azure Notification Hub notification.
     */
    public String getNotificationBody() { return notificationBody; }

    /**
     * Sets the body of the Azure Notification Hub notification.
     * @param value The body of the Azure Notification Hub notification value to set.
     */
    public void setNotificationBody(String value) { notificationBody = value; }

    /**
     * Gets the target platforms of the Azure Notification Hub notification.
     * @return The target platforms of the Azure Notification Hub notification.
     */
    public String getTargetPlatforms() { return targetPlatforms; }

    /**
     * Sets the target platforms of the Azure Notification Hub notification.
     * @param value The target platforms of the Azure Notification Hub notification value to set.
     */
    public void setTargetPlatforms(String value) { targetPlatforms = value; }

    /**
     * Gets the APNS outcome counts for the Azure Notification Hub notification.
     * @return The APNS outcome counts for the Azure Notification Hub notification.
     */
    public Map<String, Integer> getApnsOutcomeCounts() { return apnsOutcomeCounts; }

    /**
     * Sets the APNS outcome counts for the Azure Notification Hub notification.
     * @param value The APNS outcome counts for the Azure Notification Hub notification value to set.
     */
    public void setApnsOutcomeCounts(Map<String, Integer> value) { apnsOutcomeCounts = value; }

    /**
     * Gets the MPNS outcome counts for the Azure Notification Hub notification.
     * @return The MPNS outcome counts for the Azure Notification Hub notification.
     */
    public Map<String, Integer> getMpnsOutcomeCounts() { return mpnsOutcomeCounts; }

    /**
     * Sets the MPNS outcome counts for the Azure Notification Hub notification.
     * @param value The MPNS outcome counts for the Azure Notification Hub notification value to set.
     */
    public void setMpnsOutcomeCounts(Map<String, Integer> value) { mpnsOutcomeCounts = value; }

    /**
     * Gets the WNS outcome counts for the Azure Notification Hub notification.
     * @return The WNS outcome counts for the Azure Notification Hub notification.
     */
    public Map<String, Integer> getWnsOutcomeCounts() { return wnsOutcomeCounts; }

    /**
     * Sets the WNS outcome counts for the Azure Notification Hub notification.
     * @param value The WNS outcome counts for the Azure Notification Hub notification value to set.
     */
    public void setWnsOutcomeCounts(Map<String, Integer> value) { wnsOutcomeCounts = value; }

    /**
     * Gets the GCM outcome counts for the Azure Notification Hub notification.
     * @return The GCM outcome counts for the Azure Notification Hub notification.
     */
    public Map<String, Integer> getGcmOutcomeCounts() { return gcmOutcomeCounts; }

    /**
     * Sets the GCM outcome counts for the Azure Notification Hub notification.
     * @param value The GCM outcome counts for the Azure Notification Hub notification value to set.
     */
    public void setGcmOutcomeCounts(Map<String, Integer> value) { gcmOutcomeCounts = value; }

    /**
     * Gets the FCM outcome counts for the Azure Notification Hub notification.
     * @return The FCM outcome counts for the Azure Notification Hub notification.
     */
    public Map<String, Integer> getFcmOutcomeCounts() { return fcmOutcomeCounts; }

    /**
     * Sets the FCM outcome counts for the Azure Notification Hub notification.
     * @param value The FCM outcome counts for the Azure Notification Hub notification value to set.
     */
    public void setFcmOutcomeCounts(Map<String, Integer> value) { fcmOutcomeCounts = value; }

    /**
     * Gets the FCM V1 outcome counts for the Azure Notification Hub notification.
     * @return The FCM V1 outcome counts for the Azure Notification Hub notification.
     */
    public Map<String, Integer> getFcmV1OutcomeCounts() { return fcmV1OutcomeCounts; }

    /**
     * Sets the FCM V1 outcome counts for the Azure Notification Hub notification.
     * @param value The FCM V1 outcome counts for the Azure Notification Hub notification value to set.
     */
    public void setFcmV1OutcomeCounts(Map<String, Integer> value) { fcmV1OutcomeCounts = value; }

    /**
     * Gets the Baidu outcome counts for the Azure Notification Hub notification.
     * @return The Baidu outcome counts for the Azure Notification Hub notification.
     */
    public Map<String, Integer> getBaiduOutcomeCounts() { return baiduOutcomeCounts; }

    /**
     * Sets the Baidu outcome counts for the Azure Notification Hub notification.
     * @param value The Baidu outcome counts for the Azure Notification Hub notification value to set.
     */
    public void setBaiduOutcomeCounts(Map<String, Integer> value) { baiduOutcomeCounts = value; }

    /**
     * Gets the ADM outcome counts for the Azure Notification Hub notification.
     * @return The ADM outcome counts for the Azure Notification Hub notification.
     */
    public Map<String, Integer> getAdmOutcomeCounts() { return admOutcomeCounts; }

    /**
     * Sets the ADM outcome counts for the Azure Notification Hub notification.
     * @param value The ADM outcome counts for the Azure Notification Hub notification value to set.
     */
    public void setAdmOutcomeCounts(Map<String, Integer> value) { admOutcomeCounts = value; }

    /**
     * Sets the browser PNS outcome counts for the Azure Notification Hub notification.
     * @param value The browser PNS outcome counts for the Azure Notification Hub notification value to set.
     */
    public void setBrowserOutcomeCounts(Map<String, Integer> value) { browserOutcomeCounts = value; }

    /**
     * Gets the browser PNS outcome counts for the Azure Notification Hub notification.
     * @return The browser PNS outcome counts for the Azure Notification Hub notification.
     */
    public Map<String, Integer> getBrowserOutcomeCounts() { return browserOutcomeCounts; }

    /**
     * Gets the PNS Error Details container URI for the Azure Notification Hub notification.
     * @return The PNS Error Details container URI for the Azure Notification Hub notification.
     */
    public String getPnsErrorDetailsUri() { return pnsErrorDetailsUri; }

    /**
     * Sets the PNS Error Details container URI for the Azure Notification Hub notification.
     * @param value The PNS Error Details container URI for the Azure Notification Hub notification value to set.
     */
    public void setPnsErrorDetailsUri(String value) { pnsErrorDetailsUri = value; }

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
        digester.addCallMethod("*/Outcome", "put", 2, new Class[]{String.class, Integer.class});
        digester.addCallParam("*/Name", 0);
        digester.addCallParam("*/Count", 1);
        digester.addSetNext("*/ApnsOutcomeCounts", "setApnsOutcomeCounts", Map.class.getName());

        digester.addObjectCreate("*/MpnsOutcomeCounts", HashMap.class);
        digester.addCallMethod("*/Outcome", "put", 2, new Class[]{String.class, Integer.class});
        digester.addCallParam("*/Name", 0);
        digester.addCallParam("*/Count", 1);
        digester.addSetNext("*/MpnsOutcomeCounts", "setMpnsOutcomeCounts", Map.class.getName());

        digester.addObjectCreate("*/WnsOutcomeCounts", HashMap.class);
        digester.addCallMethod("*/Outcome", "put", 2, new Class[]{String.class, Integer.class});
        digester.addCallParam("*/Name", 0);
        digester.addCallParam("*/Count", 1);
        digester.addSetNext("*/WnsOutcomeCounts", "setWnsOutcomeCounts", Map.class.getName());

        digester.addObjectCreate("*/GcmOutcomeCounts", HashMap.class);
        digester.addCallMethod("*/Outcome", "put", 2, new Class[]{String.class, Integer.class});
        digester.addCallParam("*/Name", 0);
        digester.addCallParam("*/Count", 1);
        digester.addSetNext("*/GcmOutcomeCounts", "setGcmOutcomeCounts", Map.class.getName());

        digester.addObjectCreate("*/FcmOutcomeCounts", HashMap.class);
        digester.addCallMethod("*/Outcome", "put", 2, new Class[]{String.class, Integer.class});
        digester.addCallParam("*/Name", 0);
        digester.addCallParam("*/Count", 1);
        digester.addSetNext("*/FcmOutcomeCounts", "setFcmOutcomeCounts", Map.class.getName());

        digester.addObjectCreate("*/FcmV1OutcomeCounts", HashMap.class);
        digester.addCallMethod("*/Outcome", "put", 2, new Class[]{String.class, Integer.class});
        digester.addCallParam("*/Name", 0);
        digester.addCallParam("*/Count", 1);
        digester.addSetNext("*/FcmV1OutcomeCounts", "setFcmV1OutcomeCounts", Map.class.getName());

        digester.addObjectCreate("*/AdmOutcomeCounts", HashMap.class);
        digester.addCallMethod("*/Outcome", "put", 2, new Class[]{String.class, Integer.class});
        digester.addCallParam("*/Name", 0);
        digester.addCallParam("*/Count", 1);
        digester.addSetNext("*/AdmOutcomeCounts", "setAdmOutcomeCounts", Map.class.getName());

        digester.addObjectCreate("*/BaiduOutcomeCounts", HashMap.class);
        digester.addCallMethod("*/Outcome", "put", 2, new Class[]{String.class, Integer.class});
        digester.addCallParam("*/Name", 0);
        digester.addCallParam("*/Count", 1);
        digester.addSetNext("*/BaiduOutcomeCounts", "setBaiduOutcomeCounts", Map.class.getName());

        digester.addObjectCreate("*/BrowserOutcomeCounts", HashMap.class);
        digester.addCallMethod("*/Outcome", "put", 2, new Class[]{String.class, Integer.class});
        digester.addCallParam("*/Name", 0);
        digester.addCallParam("*/Count", 1);
        digester.addSetNext("*/BrowserOutcomeCounts", "setBrowserOutcomeCounts", Map.class.getName());
    }
}
