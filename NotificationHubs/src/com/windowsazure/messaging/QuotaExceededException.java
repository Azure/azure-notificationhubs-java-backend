//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.time.Duration;
import java.util.Optional;

@SuppressWarnings("serial")
public class QuotaExceededException extends NotificationHubsException{
	public static Duration DefaultDelay = Duration.ofSeconds(10);	

	public QuotaExceededException(String message, int httpStatusCode, Optional<Duration> retryAfter) {
		super(message, httpStatusCode, retryAfter);
	}
	
	@Override
	public boolean getIsTransient(){
		return true;
	}
}
