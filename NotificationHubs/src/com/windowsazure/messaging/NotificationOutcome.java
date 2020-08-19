//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

/**
 * This class represents the notification outcome.
 */
public class NotificationOutcome {
	private final String trackingId;
	private final String notificationId;

    /**
     * Creates a notification outcome with tracking ID and notification ID.
     * @param trackingId The tracking ID for the notification outcome.
     * @param notificationId The notification ID for the notification outcome.
     */
	public NotificationOutcome(String trackingId, String notificationId){
		this.trackingId = trackingId;
		this.notificationId = notificationId;
	}

    /**
     * Gets the tracking ID for the notification outcome.
     * @return The tracking ID for the notification outcome.
     */
	public String getTrackingId(){
		return this.trackingId;
	}

    /**
     * Gets the notification ID from the notification outcome.
     * @return The notification ID from the notification outcome.
     */
	public String getNotificationId(){
		return this.notificationId;
	}
}
