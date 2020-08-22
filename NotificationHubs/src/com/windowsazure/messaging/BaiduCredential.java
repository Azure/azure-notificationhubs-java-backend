//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents credentials for the Baidu PNS.
 */
public final class BaiduCredential extends PnsCredential {
    private String baiduApiKey;
    private String baiduSecretKey;
    private String baiduEndPoint;

    /**
     * Creates a Baidu credential
     */
    public BaiduCredential() {
        this(null, null);
    }

    /**
     * Creates a Baidu credential with API key and secret.
     *
     * @param baiduApiKey    The Baidu API key.
     * @param baiduSecretKey The Baidu secret key.
     */
    public BaiduCredential(String baiduApiKey, String baiduSecretKey) {
        this.baiduApiKey = baiduApiKey;
        this.baiduSecretKey = baiduSecretKey;
    }

    /**
     * Gets the Baidu API key.
     *
     * @return The Baidu API key.
     */
    public String getBaiduApiKey() {
        return this.baiduApiKey;
    }

    /**
     * Sets the Baidu API key.
     *
     * @param baiduApiKey The Baidu API key.
     */
    public void setBaiduApiKey(String baiduApiKey) {
        this.baiduApiKey = baiduApiKey;
    }

    /**
     * Gets the Baidu secret key.
     *
     * @return The Baidu secret key.
     */
    public String getBaiduSecretKey() {
        return baiduSecretKey;
    }

    /**
     * Sets the Baidu secret key.
     *
     * @param baiduSecretKey THe Baidu secret key.
     */
    public void setBaiduSecretKey(String baiduSecretKey) {
        this.baiduSecretKey = baiduSecretKey;
    }

    /**
     * Gets the Baidu endpoint
     *
     * @return The Baidu endpoint.
     */
    public String getBaiduEndPoint() {
        return baiduEndPoint;
    }

    /**
     * Sets the Baidu API endpoint
     *
     * @param baiduEndPoint The Baidu API endpoint.
     */
    public void setBaiduEndPoint(String baiduEndPoint) {
        this.baiduEndPoint = baiduEndPoint;
    }

    @Override
    public List<SimpleEntry<String, String>> getProperties() {
        ArrayList<SimpleEntry<String, String>> result = new ArrayList<>();
        result.add(new SimpleEntry<>("BaiduApiKey", getBaiduApiKey()));
        result.add(new SimpleEntry<>("BaiduSecretKey", getBaiduSecretKey()));
        return result;
    }

    @Override
    public String getRootTagName() {
        return "BaiduCredential";
    }
}
