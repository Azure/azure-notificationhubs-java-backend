//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.time.Duration;

@SuppressWarnings("serial")
public class NotificationHubsException extends Exception {
	private int httpStatusCode;
	protected boolean isTransient;
	protected Duration retryAfter;
	
	public NotificationHubsException(String message, int httpStatusCode){
		super(message);
		this.httpStatusCode=httpStatusCode;
	}
	
	public int getHttpStatusCode(){
		return this.httpStatusCode;
	}
	
	public boolean getIsTransient(){
		return this.isTransient;
	}
}