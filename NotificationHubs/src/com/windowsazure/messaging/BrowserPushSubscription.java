//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import com.google.gson.GsonBuilder;

/**
 * This class represents a Browser Push subscription.
 */
public final class BrowserPushSubscription {

    private String endpoint;
    private String p256dh;
    private String auth;

    /**
     * Creates a new instance of the BrowserPushSubscription class.
     */
    public BrowserPushSubscription() {
    }

    /**
     * Creates a new instance of the BrowserPushSubscription class.
     *
     * @param endpoint The endpoint from browser PNS.
     * @param p256dh The public key from browser PNS.
     * @param auth The authentication secret from browser PNS.
     */
    public BrowserPushSubscription(String endpoint, String p256dh, String auth) {
        setEndpoint(endpoint);
        setP256dh(p256dh);
        setAuth(auth);
    }

    /**
     * Gets the endpoint for browser PNS.
     * @return The endpoint for browser PNS.
     */
    public String getEndpoint() { return endpoint; }

    /**
     * Sets the endpoint for browser PNS.
     * @param value The endpoint value to set.
     */
    public void setEndpoint(String value) { endpoint = value; }

    /**
     * Gets the P256DH public key for browser PNS.
     * @return The P256DH public key for browser PNS.
     */
    public String getP256dh() { return p256dh; }

    /**
     * Sets the P256Dh public key for browser PNS.
     * @param value The P256DH public key value to set.
     */
    public void setP256dh(String value) { p256dh = value; }

    /**
     * Gets the authentication secret for browser PNS.
     * @return The authentication secret for browser PNS.
     */
    public String getAuth() { return auth; }

    /**
     * Sets the authentication secret for browser PNS.
     * @param value The authentication secret value to set.
     */
    public void setAuth(String value) { auth = value; }

    /**
     * Returns the JSON-serialized representation of the browser push subscription.
     * @return JSON-serialized representation of the browser push subscription.
     */
    public String toJson() {
        return new GsonBuilder().disableHtmlEscaping().create().toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BrowserPushSubscription that = (BrowserPushSubscription) o;
        if (endpoint == null && that.getEndpoint() != null) { return false; }
        if (p256dh == null && that.getP256dh() != null) { return false; }
        if (auth == null && that.getAuth() != null) { return false; }
        return endpoint.equals(that.getEndpoint()) && p256dh.equals(that.getP256dh()) && auth.equals(that.getAuth());
    }
}
