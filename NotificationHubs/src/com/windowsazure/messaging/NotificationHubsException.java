//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.time.Duration;
import java.util.Optional;

@SuppressWarnings("serial")
public class NotificationHubsException extends Exception {
	private int httpStatusCode;
	private Optional<Duration> retryAfter;
		
	public NotificationHubsException(String message, int httpStatusCode, Optional<Duration> retryAfter){
		super(message);
		this.httpStatusCode=httpStatusCode;
		this.retryAfter = retryAfter;
	}
	
	public int getHttpStatusCode(){
		return this.httpStatusCode;
	}
	
	public boolean getIsTransient(){
		return false;
	}

	public Optional<Duration> getRetryAfter() {
		return this.retryAfter;
	}
}