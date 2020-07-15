//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated use {@link com.windowsazure.messaging.FcmCredential#FcmCredential()} instead.
 */
 
@Deprecated
public final class GcmCredential extends PnsCredential {	
	private String googleApiKey;
	
	public GcmCredential(){
		this(null);
	}
		
	public GcmCredential(String googleApiKey){
		super();
		this.setGoogleApiKey(googleApiKey);
	}
	
	public String getGoogleApiKey() {
		return googleApiKey;
	}

   	public void setgoogleApiKey(String googleApiKey) {
        	this.googleApiKey = googleApiKey; // fix for reflection that's calling 'setgoogleApiKey' instead of 'setGoogleApiKey'.
    	}

	public void setGoogleApiKey(String googleApiKey) {
		this.googleApiKey = googleApiKey;
	}	
		
	@Override
	public List<SimpleEntry<String, String>> getProperties() {
		ArrayList<SimpleEntry<String, String>> result = new ArrayList<SimpleEntry<String, String>>();
		result.add(new SimpleEntry<String, String>("GoogleApiKey",getGoogleApiKey()));
		return result;
	}

	@Override
	public String getRootTagName() {
		return "GcmCredential";
	}
}
