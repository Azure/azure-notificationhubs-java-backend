//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated use {@link com.windowsazure.messaging.FcmV1Credential#FcmV1Credential()} instead.
 */
@Deprecated
public final class GcmCredential extends PnsCredential {
    private String googleApiKey;
    private String gcmEndpoint;

    /**
     * Creates a new instance of the GcmCredential class.
     */
    public GcmCredential() {
        super();
    }

    /**
     * Creates a new instance of the GcmCredential class.
     * @param googleApiKey The Google Cloud Messaging API key.
     */
    public GcmCredential(String googleApiKey) {
        super();
        this.setGoogleApiKey(googleApiKey);
    }

    /**
     * Creates a new instance of the GcmCredential class.
     * @param googleApiKey The Google API key from Firebase.
     * @param gcmEndpoint The GCM Endpoint for firebase.
     */
    public GcmCredential(String googleApiKey, String gcmEndpoint) {
        super();
        this.googleApiKey = googleApiKey;
        this.gcmEndpoint = gcmEndpoint;
    }

    /**
     * Gets the Google Cloud Messaging API key.
     * @return The Google Cloud Messaging API key.
     */
    public String getGoogleApiKey() { return googleApiKey; }

    /**
     * Sets the Google Cloud Messaging API key.
     * @param value The Google Cloud Messaging API key to set.
     */
    public void setGoogleApiKey(String value) { this.googleApiKey = value; }

    /**
     * Sets the Google Cloud Messaging API key.
     * @param googleApiKey The Google Cloud Messaging API key to set.
     *
     * @deprecated use {@link #setGoogleApiKey(String) setGoogleApiKey} instead.
     */
    @Deprecated
    public void setgoogleApiKey(String googleApiKey) {
        this.googleApiKey = googleApiKey; // fix for reflection that's calling 'setgoogleApiKey' instead of 'setGoogleApiKey'.
    }

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
