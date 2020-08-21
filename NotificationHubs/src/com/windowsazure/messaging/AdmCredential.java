//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents credentials for ADM for Amazon Kindle.
 */
public final class AdmCredential extends PnsCredential {
	private String clientId;
	private String clientSecret;

	/**
	 * Creates a new ADM credential.
	 */
	public AdmCredential() {
		this(null,null);
	}

	/**
	 * Creates an ADM credential with client ID and secret.
	 * @param clientId The client ID.
	 * @param clientSecret The client secret.
	 */
	public AdmCredential(String clientId, String clientSecret) {
		super();
		this.setClientId(clientId);
		this.setClientSecret(clientSecret);
	}

	/**
	 * Gets the ADM client ID.
	 * @return The ADM client ID.
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * Sets the ADm client ID.
	 * @param clientId The ADM client ID.
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * Gets the ADM client secret.
	 * @return The ADM client secret.
	 */
	public String getClientSecret() {
		return clientSecret;
	}

	/**
	 * Sets the ADM client secret.
	 * @param clientSecret The ADM client secret.
	 */
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	@Override
	public List<SimpleEntry<String, String>> getProperties() {
		ArrayList<SimpleEntry<String, String>> result = new ArrayList<>();
		result.add(new SimpleEntry<>("ClientId", getClientId()));
		result.add(new SimpleEntry<>("ClientSecret", getClientSecret()));
		return result;
	}

	@Override
	public String getRootTagName() {
		return "AdmCredential";
	}
}
