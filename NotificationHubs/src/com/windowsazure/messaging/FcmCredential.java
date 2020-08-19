//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents Firebase Cloud Messaging PNS credentials.
 */
public final class FcmCredential extends PnsCredential {
	private String googleApiKey;

    /**
     * Creates a new FCM credential.
     */
	public FcmCredential() {
		this(null);
	}

    /**
     * Creates a new FCM Credential with a Google API key.
     * @param googleApiKey The Google API key.
     */
	public FcmCredential(String googleApiKey) {
		super();
		this.setGoogleApiKey(googleApiKey);
	}

    /**
     * Gets the Google API key.
     * @return The Google API key.
     */
	public String getGoogleApiKey() {
		return googleApiKey;
	}

    /**
     * Sets the Google API key.
     * @param googleApiKey The Google API key.
     */
	public void setGoogleApiKey(String googleApiKey) {
		this.googleApiKey = googleApiKey;
	}

	@Override
	public List<SimpleEntry<String, String>> getProperties() {
		ArrayList<SimpleEntry<String, String>> result = new ArrayList<>();
		result.add(new SimpleEntry<>("GoogleApiKey", getGoogleApiKey()));
		return result;
	}

	@Override
	public String getRootTagName() {
		return "GcmCredential"; // TODO rename to FcmCredential when new version of backend will be released
	}
}
