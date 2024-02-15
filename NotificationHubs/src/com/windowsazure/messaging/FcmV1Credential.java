//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents Azure Notification Hubs credentials for Firebase Messaging V1.
 */
public final class FcmV1Credential extends PnsCredential {

    private String privateKey;
    private String projectId;
    private String clientEmail;

    /**
     * Creates a new instance of the FcmV1Credential class.
     */
    public FcmV1Credential() {

    }

    /**
     * Creates a new instance of the FcmV1Credential class.
     * @param privateKey The Private key from Firebase.
     * @param projectId The Project ID from Firebase.
     * @param clientEmail The Client Email from Firebase.
     */
    public FcmV1Credential(String privateKey, String projectId, String clientEmail) {
        super();
        this.privateKey = privateKey;
        this.projectId = projectId;
        this.clientEmail = clientEmail;
    }

    /**
     * Gets the Private key for Firebase Messaging.
     * @return The Private key for Firebase Messaging.
     */
    public String getPrivateKey() { return privateKey; }

    /**
     * Sets the Private key for Firebase Messaging.
     * @param value The Private key for Firebase Messaging to set.
     */
    public void setPrivateKey(String value) { privateKey = value; }

    /**
     * Gets the Project ID for Firebase Messaging.
     * @return The Project ID for Firebase Messaging.
     */
    public String getProjectId() { return projectId; }

    /**
     * Sets the Project ID for Firebase Messaging.
     * @param value The Project ID for Firebase Messaging to set.
     */
    public void setProjectId(String value) { projectId = value; }

    /**
     * Gets the Client Email for Firebase Messaging.
     * @return The Client Email for Firebase Messaging.
     */
    public String getClientEmail() { return clientEmail; }

    /**
     * Sets the Client Email for Firebase Messaging.
     * @param value The Client Email for Firebase Messaging to set.
     */
    public void setClientEmail(String value) { clientEmail = value; }

    @Override
    public List<SimpleEntry<String, String>> getProperties() {
        ArrayList<SimpleEntry<String, String>> result = new ArrayList<>();
        result.add(new SimpleEntry<>("PrivateKey", getPrivateKey()));
        result.add(new SimpleEntry<>("ProjectId", getProjectId()));
        result.add(new SimpleEntry<>("ClientEmail", getClientEmail()));
        return result;
    }

    @Override
    public String getRootTagName() {
        return "FcmV1Credential";
    }
}
