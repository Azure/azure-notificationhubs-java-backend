//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.Optional;

@SuppressWarnings("serial")
public class NotificationHubsException extends Exception {
	private int httpStatusCode;
	protected boolean isTransient;
	protected Optional<Integer> retryAfter;
		
	public NotificationHubsException(String message, int httpStatusCode, Optional<Integer> retryAfter){
		super(message);
		this.httpStatusCode=httpStatusCode;
		this.retryAfter = retryAfter;
	}
	
	public int getHttpStatusCode(){
		return this.httpStatusCode;
	}
	
	public boolean getIsTransient(){
		return this.isTransient;
	}
}