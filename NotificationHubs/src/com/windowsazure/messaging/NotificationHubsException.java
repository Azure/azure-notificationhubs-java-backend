package com.windowsazure.messaging;

@SuppressWarnings("serial")
public class NotificationHubsException extends Exception {
	private int httpStatusCode;
		
	public NotificationHubsException(String message, int httpStatusCode){
		super(message);
		this.httpStatusCode=httpStatusCode;
	}
	
	public int getHttpStatusCode(){
		return this.httpStatusCode;
	}
}
