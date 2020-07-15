//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

@SuppressWarnings("serial")
public class QuotaExceededException extends NotificationHubsException{
	
    /**
     * A boolean indicating if the exception is a transient error or not.
     * getting a true from this property implies that user can retry the operation
     * that generated the exception without additional intervention.
     */
	private boolean isTransient;

	public QuotaExceededException(String message, int httpStatusCode) {
		super(message, httpStatusCode);
		this.isTransient=true;
	}
	
	public boolean getIsTransient(){
		return this.isTransient;
	}
}
