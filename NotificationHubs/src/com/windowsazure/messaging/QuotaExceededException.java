//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.time.Duration;
import java.util.Optional;

@SuppressWarnings("serial")
public class QuotaExceededException extends NotificationHubsException{
	
	private Duration DefaultRetryTimeout = Duration.ofSeconds(10);
	
	public QuotaExceededException(String message, int httpStatusCode, Optional<Integer> retryAfter) {
		super(message, httpStatusCode);
		this.isTransient = true;
		this.retryAfter = retryAfter.isPresent() ? Duration.ofSeconds(retryAfter.get()) : DefaultRetryTimeout;
	}
	
	public boolean getIsTransient(){
		return this.isTransient;
	}
	
	public Duration getRetryAfter(){
		return this.retryAfter;
	}
}
