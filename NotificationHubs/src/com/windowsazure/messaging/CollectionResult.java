//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.LinkedList;
import java.util.List;

/**
 * Class representing the result of a query returning a set of registrations.
 *
 */
public class CollectionResult {
	private String continuationToken;
	private final List<Registration> registrations = new LinkedList<>();

    /**
     * Creates a new collection result.
     */
	public CollectionResult() {
		// TODO Auto-generated constructor stub
	}

    /**
     * Adds a registration to the collection result.
     * @param registration The registration to add to the collection result.
     */
	public void addRegistration(Registration registration) {
		registrations.add(registration);
	}

    /**
     * Gets all registrations from the collection result.
     * @return The registrations from the collection result.
     */
	public List<Registration> getRegistrations() {
		return registrations;
	}

	/**
	 * Gets the continuation token for this result. If the continuation is null,
	 * then there are no more registration in this result set.
	 *
	 * @return continuation token
	 */
	public String getContinuationToken() {
		return continuationToken;
	}

    /**
     * Sets the continuation token for this result.  If the continuation is null, there are no more results.
     * @param continuationToken The continuation token for the collection result.
     */
	public void setContinuationToken(String continuationToken) {
		this.continuationToken = continuationToken;
	}
}
