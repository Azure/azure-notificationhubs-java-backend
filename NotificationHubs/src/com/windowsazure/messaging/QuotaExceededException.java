//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.time.Duration;
import java.util.Optional;

@SuppressWarnings("serial")
public class QuotaExceededException extends NotificationHubsException{
	public static Duration DefaultDelay = Duration.ofSeconds(10);	

	public QuotaExceededException(String message, int httpStatusCode, Optional<Integer> retryAfter) {
		super(message, httpStatusCode, retryAfter);
		this.isTransient = true;
	}
	
	public boolean getIsTransient(){
		return this.isTransient;
	}
}
