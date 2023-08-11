//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents Baidu PNS credentials for Azure Notification Hubs.
 */
public final class BaiduCredential extends PnsCredential {
    private String baiduApiKey;
    private String baiduSecretKey;
    private String baiduEndPoint;

    /**
     * Creates a new instance of the BaiduCredential class.
     */
    public BaiduCredential() {
    }

    /**
     * Creates a new instance of the BaiduCredential class with the Baidu API key and secret key.
     * @param baiduApiKey The Baidu API key.
     * @param baiduSecretKey The Baidu secret key.
     */
    public BaiduCredential(String baiduApiKey, String baiduSecretKey) {
        this.baiduApiKey = baiduApiKey;
        this.baiduSecretKey = baiduSecretKey;
    }

    /**
     * Gets the Baidu API key.
     * @return The Baidu API key.
     */
    public String getBaiduApiKey() { return this.baiduApiKey; }

    /**
     * Sets the Baidu API key.
     * @param value The Baidu API key to set.
     */
    public void setBaiduApiKey(String value) { baiduApiKey = value; }

    /**
     * Gets the Baidu secret key.
     * @return The Baidu secret key.
     */
    public String getBaiduSecretKey() { return baiduSecretKey; }

    /**
     * Sets the Baidu secret key.
     * @param value The Baidu secret key to set.
     */
    public void setBaiduSecretKey(String value) { baiduSecretKey = value; }

    /**
     * Gets the Baidu URL endpoint.
     * @return The Baidu URL endpoint.
     */
    public String getBaiduEndPoint() { return baiduEndPoint; }

    /**
     * Sets the Baidu URL endpoint.
     * @param value The Baidu URL endpoint to set.
     */
    public void setBaiduEndPoint(String value) { baiduEndPoint = value; }

    @Override
    public List<SimpleEntry<String, String>> getProperties() {
        ArrayList<SimpleEntry<String, String>> result = new ArrayList<>();
        if (getBaiduApiKey() != null || getBaiduSecretKey() != null) {
            result.add(new SimpleEntry<>("BaiduApiKey", getBaiduApiKey()));
            result.add(new SimpleEntry<>("BaiduSecretKey", getBaiduSecretKey()));
        }
        return result;
    }

    @Override
    public String getRootTagName() {
        return "BaiduCredential";
    }
}
