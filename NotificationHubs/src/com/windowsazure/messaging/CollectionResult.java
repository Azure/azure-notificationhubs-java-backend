//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.LinkedList;
import java.util.List;

/**
 * Class representing the result of a query returning a set of registrations.
 */
public class CollectionResult {
    private String continuationToken;
    private final List<Registration> registrations = new LinkedList<>();

    /**
     * Creates a new instance of the CollectionResult class.
     */
    public CollectionResult() {

    }

    CollectionResult(List<Registration> registrations, String continuationToken) {
        this.registrations.addAll(registrations);
        this.continuationToken = continuationToken;
    }

    /**
     * Gets the registrations from the list.
     * @return The registrations from the list.
     */
    public List<Registration> getRegistrations() { return registrations; }

    /**
     * Gets the continuation token for this result. If the continuation is null,
     * then there are no more registration in this result set.
     *
     * @return The continuation token
     */
    public String getContinuationToken() {
        return continuationToken;
    }

    /**
     * Sets the continuation token.
     * @param value The continuation token.
     */
    void setContinuationToken(String value) {
        continuationToken = value;
    }

    public void addRegistration(Registration registration) { registrations.add(registration); }
}
