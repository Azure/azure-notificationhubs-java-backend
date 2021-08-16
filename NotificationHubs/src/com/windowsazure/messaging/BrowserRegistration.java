//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.Objects;

/**
 * This class represents a Browser Push device registration.
 */
public class BrowserRegistration extends Registration {
    private static final String BROWSER_NATIVE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><BrowserRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String BROWSER_NATIVE_REGISTRATION2 = "<Endpoint>";
    private static final String BROWSER_NATIVE_REGISTRATION3 = "</Endpoint><P256DH>";
    private static final String BROWSER_NATIVE_REGISTRATION4 = "</P256DH><Auth>";
    private static final String BROWSER_NATIVE_REGISTRATION5 = "</Auth></BrowserRegistrationDescription></content></entry>";

    protected String endpoint;
    protected String p256dh;
    protected String auth;

    /**
     * Creates a new instance of the BrowserRegistration class.
     */
    public BrowserRegistration() {
        super();
    }

    /**
     * Creates a new instance of the BrowserRegistration class with endpoint, p256dh and auth secret.
     * @param endpoint The browser PNS endpoint URL.
     * @param p256dh The P256DH key from the browser registration.
     * @param auth The auth secret from the browser registration.
     */
    public BrowserRegistration(String endpoint, String p256dh, String auth) {
        super();
        this.endpoint = endpoint;
        this.p256dh = p256dh;
        this.auth = auth;
    }

    /**
     * Creates a new instance of the BrowserRegistration class with endpoint, p256dh and auth secret.
     * @param registrationId The Azure Notification Hubs registration ID.
     * @param endpoint The browser PNS endpoint URL.
     * @param p256dh The P256DH key from the browser registration.
     * @param auth The auth secret from the browser registration.
     */
    public BrowserRegistration(String registrationId, String endpoint, String p256dh, String auth) {
        super(registrationId);
        this.endpoint = endpoint;
        this.p256dh = p256dh;
        this.auth = auth;
    }

    /**
     * Gets the browser PNS endpoint URL.
     * @return The browser PNS endpoint URL.
     */
    public String getEndpoint() { return endpoint; }

    /**
     * Sets the browser PNS endpoint URL.
     * @param value The browser PNS endpoint URL.
     */
    public void setEndpoint(String value) { endpoint = value; }

    /**
     * Gets the browser subscription p256dh key.
     * @return The browser subscription p256dh key.
     */
    public String getP256dh() { return p256dh; }

    /**
     * Sets the browser subscription p256dh key.
     * @param value The browser subscription p256dh key.
     */
    public void setP256dh(String value) { p256dh = value; }

    /**
     * Gets the browser subscription auth secret.
     * @return The browser subscription auth secret.
     */
    public String getAuth() { return auth; }

    /**
     * Sets the browser subscription auth secret.
     * @param value The browser subscription auth secret.
     */
    public void setAuth(String value) { auth = value; }

    /**
     * Gets the PNS handle for getting devices by channel.
     * @return The PNS handle for getting devices by channel.
     */
    @Override
    public String getPnsHandle() { return endpoint; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BrowserRegistration that = (BrowserRegistration) o;
        return Objects.equals(getEndpoint(), that.getEndpoint()) && Objects.equals(getP256dh(), that.getP256dh()) && Objects.equals(getAuth(), that.getAuth());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEndpoint(), getP256dh(), getAuth());
    }

    @Override
    public String getXml() {
        return BROWSER_NATIVE_REGISTRATION1 +
            getTagsXml() +
            BROWSER_NATIVE_REGISTRATION2 +
            endpoint +
            BROWSER_NATIVE_REGISTRATION3 +
            p256dh +
            BROWSER_NATIVE_REGISTRATION4 +
            auth +
            BROWSER_NATIVE_REGISTRATION5;
    }
}
