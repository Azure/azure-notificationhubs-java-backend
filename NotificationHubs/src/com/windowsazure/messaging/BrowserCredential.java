//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents Azure Notification Hubs credentials for browser push.
 */
public final class BrowserCredential extends PnsCredential {

    private String subject;
    private String vapidPublicKey;
    private String vapidPrivateKey;

    /**
     * Creates a new instance of the BrowserCredential class.
     */
    public BrowserCredential() {
    }

    /**
     * Creates a new instance of the BrowserCredential class.
     * @param subject A mailto address associated with the website.
     * @param vapidPublicKey The VAPID public key.
     * @param vapidPrivateKey The VAPID private key.
     */
    public BrowserCredential(String subject, String vapidPublicKey, String vapidPrivateKey) {
        super();
        setSubject(subject);
        setVapidPublicKey(vapidPublicKey);
        setVapidPrivateKey(vapidPrivateKey);
    }

    /**
     * Gets the browser push subject.
     * @return The browser push subject.
     */
    public String getSubject() { return subject; }

    /**
     * Sets the browser push subject.
     * @param value The browser push subject.
     */
    public void setSubject(String value) { subject = value; }

    /**
     * Gets the VAPID public key.
     * @return The VAPID public key.
     */
    public String getVapidPublicKey() { return vapidPublicKey; }

    /**
     * Sets the VAPID public key.
     * @param value The VAPID public key.
     */
    public void setVapidPublicKey(String value) { vapidPublicKey = value; }

    /**
     * Gets the VAPID private key.
     * @return The VAPID private key.
     */
    public String getVapidPrivateKey() { return vapidPrivateKey; }

    /**
     * Sets the VAPID private key.
     * @param value The VAPID private key.
     */
    public void setVapidPrivateKey(String value) { vapidPrivateKey = value; }

    @Override
    public List<SimpleEntry<String, String>> getProperties() {
        ArrayList<SimpleEntry<String, String>> result = new ArrayList<>();
        result.add(new SimpleEntry<>("Subject", getSubject()));
        result.add(new SimpleEntry<>("VapidPublicKey", getVapidPublicKey()));
        result.add(new SimpleEntry<>("VapidPrivateKey", getVapidPrivateKey()));
        return result;
    }

    @Override
    public String getRootTagName() {
        return "BrowserCredential";
    }
}
