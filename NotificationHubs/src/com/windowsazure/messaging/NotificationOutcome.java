//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

public class NotificationOutcome {
	private final String trackingId;
	private final String notificationId;

	public NotificationOutcome(String trackingId, String notificationId){
		this.trackingId = trackingId;
		this.notificationId = notificationId;
	}

	public String getTrackingId(){
		return this.trackingId;
	}

	public String getNotificationId(){
		return this.notificationId;
	}
}
