//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents Amazon PNS credentials.
 */
public final class AdmCredential extends PnsCredential {
    private String clientId;
    private String clientSecret;

    /**
     * Creates a new instance of the AdmCredential class.
     */
    public AdmCredential() {
        super();
    }

    /**
     * Creates a new instance of the AdmCredential class.
     * @param clientId The Amazon client ID.
     * @param clientSecret The Amazon client secret.
     */
    public AdmCredential(String clientId, String clientSecret) {
        super();
        this.setClientId(clientId);
        this.setClientSecret(clientSecret);
    }

    /**
     * Gets the Amazon client ID.
     * @return The Amazon client ID.
     */
    public String getClientId() { return clientId; }

    /**
     * Gets the Amazon client ID.
     * @param value THe Amazon client ID to set.
     */
    public void setClientId(String value) { clientId = value; }

    /**
     * Gets the Amazon client secret.
     * @return The Amazon client secret.
     */
    public String getClientSecret() { return clientSecret; }

    /**
     * Sets the Amazon client secret.
     * @param value The Amazon client secret to set.
     */
    public void setClientSecret(String value) { clientSecret = value; }

    @Override
    public List<SimpleEntry<String, String>> getProperties() {
        ArrayList<SimpleEntry<String, String>> result = new ArrayList<>();
        if (getClientId() != null || getClientSecret() != null) {
            result.add(new SimpleEntry<>("ClientId", getClientId()));
            result.add(new SimpleEntry<>("ClientSecret", getClientSecret()));
        }
        return result;
    }

    @Override
    public String getRootTagName() {
        return "AdmCredential";
    }
}
