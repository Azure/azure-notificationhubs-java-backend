//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents Azure Notification Hubs credentials for Firebase Messaging.
 * @deprecated use {@link com.windowsazure.messaging.FcmV1Credential#FcmV1Credential()} instead.
 */
public final class FcmCredential extends PnsCredential {

    private String googleApiKey;
    private String gcmEndpoint;

    /**
     * Creates a new instance of the FcmCredential class.
     */
    public FcmCredential() {

    }

    /**
     * Creates a new instance of the FcmCredential class.
     * @param googleApiKey The Google API key from Firebase.
     */
    public FcmCredential(String googleApiKey) {
        super();
        this.googleApiKey = googleApiKey;
    }

    /**
     * Creates a new instance of the FcmCredential class.
     * @param googleApiKey The Google API key from Firebase.
     * @param gcmEndpoint The GCM Endpoint for firebase.
     */
    public FcmCredential(String googleApiKey, String gcmEndpoint) {
        super();
        this.googleApiKey = googleApiKey;
        this.gcmEndpoint = gcmEndpoint;
    }

    /**
     * Gets the Google API key for Firebase Messaging.
     * @return The Google API key for Firebase Messaging.
     */
    public String getGoogleApiKey() { return googleApiKey; }

    /**
     * Sets the Google API key for Firebase Messaging.
     * @param value The Google API key for Firebase Messaging to set.
     */
    public void setGoogleApiKey(String value) { googleApiKey = value; }

    /**
     * Gets the GCM Endpoint for Firebase Messaging.
     * @return The GCM Endpoint for Firebase Messaging.
     */
    public String getGcmEndpoint() { return gcmEndpoint; }

    /**
     * Sets the GCM Endpoint for Firebase Messaging.
     * @param value The GCM Endpoint for Firebase Messaging to set.
     */
    public void setGcmEndpoint(String value) { gcmEndpoint = value; }

    @Override
    public List<SimpleEntry<String, String>> getProperties() {
        ArrayList<SimpleEntry<String, String>> result = new ArrayList<>();
        result.add(new SimpleEntry<>("GcmEndpoint", getGcmEndpoint()));
        result.add(new SimpleEntry<>("GoogleApiKey", getGoogleApiKey()));
        return result;
    }

    @Override
    public String getRootTagName() {
        return "GcmCredential";
    }
}
