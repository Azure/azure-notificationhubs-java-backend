package com.windowsazure.messaging;

public class QuotaExceededException extends NotificationHubsException{

	public QuotaExceededException(String message, int httpStatusCode) {
		super(message, httpStatusCode);
		
	}

}
