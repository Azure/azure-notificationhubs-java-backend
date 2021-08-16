package com.windowsazure.messaging;

import java.util.Objects;

/**
 * This class represents
 */
public class BrowserPushChannel {
    private String endpoint;
    private String p256dh;
    private String auth;

    /**
     * Creates a new instance of the BrowserPushChannel class.
     */
    public BrowserPushChannel() {

    }

    /**
     * Creates a new instance of the BrowserPushChannel class.
     * @param endpoint The browser PNS endpoint URL.
     * @param p256dh The P256DH key from the browser registration.
     * @param auth The auth secret from the browser registration.
     */
    public BrowserPushChannel(
        String endpoint,
        String p256dh,
        String auth
    ) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrowserPushChannel that = (BrowserPushChannel) o;
        return Objects.equals(getEndpoint(), that.getEndpoint()) && Objects.equals(getP256dh(), that.getP256dh()) && Objects.equals(getAuth(), that.getAuth());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEndpoint(), getP256dh(), getAuth());
    }
}
