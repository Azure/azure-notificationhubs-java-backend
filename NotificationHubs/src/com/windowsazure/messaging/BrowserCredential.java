//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents Browser Push PNS credentials for Azure Notification Hubs.
 */
public final class BrowserCredential extends PnsCredential {
    private String subject;
    private String vapidPublicKey;
    private String vapidPrivateKey;

    /**
     * Creates a new BrowserCredential with default values.
     */
    public BrowserCredential() {
    }

    /**
     * Creates a BrowserCredential with subject, VAPID public key and private key.
     * @param subject The subject in mailto: or http:// form.
     * @param vapidPublicKey The VAPID public key.
     * @param vapidPrivateKey The VAPID private key.
     */
    public BrowserCredential(String subject, String vapidPublicKey, String vapidPrivateKey) {
        this.subject = subject;
        this.vapidPublicKey = vapidPublicKey;
        this.vapidPrivateKey = vapidPrivateKey;
    }

    /**
     * Gets the browser credential subject.
     * @return The browser credential subject.
     */
    public String getSubject() { return subject; }

    /**
     * Sets the browser credential subject.
     * @param value The new browser credential subject to set.
     */
    public void setSubject(String value) { subject = value; }

    /**
     * Gets the browser credential VAPID public key.
     * @return The browser credential VAPID public key.
     */
    public String getVapidPublicKey() { return vapidPublicKey; }

    /**
     * Sets the browser credential VAPID public key.
     * @param value The browser credential VAPID public key to set.
     */
    public void setVapidPublicKey(String value) { vapidPublicKey = value; }

    /**
     * Gets the browser credential VAPID private key.
     * @return The browser credential VAPID private key.
     */
    public String getVapidPrivateKey() { return vapidPrivateKey; }

    /**
     * Sets the browser credential VAPID private key.
     * @param value The browser credential VAPID private key to set.
     */
    public void setVapidPrivateKey(String value) { vapidPrivateKey = value; }

    @Override
    public List<AbstractMap.SimpleEntry<String, String>> getProperties() {
        ArrayList<AbstractMap.SimpleEntry<String, String>> result = new ArrayList<>();
        result.add(new AbstractMap.SimpleEntry<>("Subject", subject));
        result.add(new AbstractMap.SimpleEntry<>("VapidPublicKey", vapidPublicKey));
        result.add(new AbstractMap.SimpleEntry<>("VapidPrivateKey", vapidPrivateKey));
        return result;
    }

    @Override
    public String getRootTagName() {
        return "BrowserCredential";
    }
}
