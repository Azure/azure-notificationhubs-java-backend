//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.Objects;

/**
 * Class representing a native registration for devices using browser push.
 */
public class BrowserRegistration extends Registration {
    private static final String BROWSER_NATIVE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><BrowserRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String BROWSER_NATIVE_REGISTRATION2 = "<Endpoint>";
    private static final String BROWSER_NATIVE_REGISTRATION3 = "</Endpoint><P256DH>";
    private static final String BROWSER_NATIVE_REGISTRATION4 = "</P256DH><Auth>";
    private static final String BROWSER_NATIVE_REGISTRATION5 = "</Auth></BrowserRegistrationDescription></content></entry>";

    protected BrowserPushSubscription browserPushSubscription;

    /**
     * Creates a new instance of the BrowserRegistration class.
     */
    public BrowserRegistration() {
        super();
        this.browserPushSubscription = new BrowserPushSubscription();
    }

    /**
     * Creates a new instance of the BrowserRegistration with a registration ID and browser push subscription.
     * @param registrationId The Azure Notification Hubs registration ID.
     * @param browserPushSubscription The browser push subscription.
     */
    public BrowserRegistration(String registrationId, BrowserPushSubscription browserPushSubscription) {
        super(registrationId);
        this.browserPushSubscription = browserPushSubscription;
    }

    /**
     * Creates a new instance of the BrowserRegistration with a browser push subscription.
     * @param browserPushSubscription The browser push subscription.
     */
    public BrowserRegistration(BrowserPushSubscription browserPushSubscription) {
        super();
        this.browserPushSubscription = browserPushSubscription;
    }

    /**
     * Gets the browser push subscription.
     * @return The browser push subscription.
     */
    public BrowserPushSubscription getBrowserPushSubscription() { return browserPushSubscription; }

    /**
     * Sets the browser push subscription.
     * @param value The browser push subscription to set.
     */
    public void setBrowserPushSubscription(BrowserPushSubscription value) { browserPushSubscription = value; }

    /**
     * Sets the browser push subscription endpoint.
     * @param value The browser push subscription endpoint.
     */
    public void setEndpoint(String value) { browserPushSubscription.setEndpoint(value); }

    /**
     * Sets the browser push P256DH public key.
     * @param value The browser push subscription P256DH public key.
     */
    public void setP256dh(String value) { browserPushSubscription.setP256dh(value); }

    /**
     * Sets the browser push subscription authentication secret.
     * @param value The browser push subscription authentication secret.
     */
    public void setAuth(String value) {browserPushSubscription.setAuth((value)); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BrowserRegistration that = (BrowserRegistration) o;
        return Objects.equals(browserPushSubscription, that.getBrowserPushSubscription());
    }

    /**
     * Gets the PNS handle for getting devices by channel.
     * @return The PNS handle for getting devices by channel.
     */
    @Override
    public String getPnsHandle() { return browserPushSubscription.toJson(); }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), browserPushSubscription.getEndpoint(), browserPushSubscription.getP256dh(), browserPushSubscription.getAuth());
    }

    @Override
    public String getXml() {
        return BROWSER_NATIVE_REGISTRATION1 +
            getTagsXml() +
            BROWSER_NATIVE_REGISTRATION2 +
            browserPushSubscription.getEndpoint() +
            BROWSER_NATIVE_REGISTRATION3 +
            browserPushSubscription.getP256dh() +
            BROWSER_NATIVE_REGISTRATION4 +
            browserPushSubscription.getAuth() +
            BROWSER_NATIVE_REGISTRATION5;
    }

}
