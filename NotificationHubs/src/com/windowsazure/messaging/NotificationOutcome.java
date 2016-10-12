package com.windowsazure.messaging;

public class NotificationOutcome {
	private String trackingId;
	private String notificationId;
	
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
