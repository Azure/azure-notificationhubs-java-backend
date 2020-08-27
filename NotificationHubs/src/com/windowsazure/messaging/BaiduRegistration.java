//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

/**
 * Class representing a native registration for devices using Baidu PNS.
 */

public class BaiduRegistration extends Registration {
    private static final String BAIDU_NATIVE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><BaiduRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String BAIDU_NATIVE_REGISTRATION2 = "<BaiduUserId>";
    private static final String BAIDU_NATIVE_REGISTRATION3 = "</BaiduUserId><BaiduChannelId>";
    private static final String BAIDU_NATIVE_REGISTRATION4 = "</BaiduChannelId></BaiduRegistrationDescription></content></entry>";

    protected String baiduUserId;
    protected String baiduChannelId;

    /**
     * Creates a new Baidu registration.
     */
    public BaiduRegistration() {
        this(null, null);
    }

    /**
     * Creates a new Baidu registration with Baidu user ID and channel ID.
     *
     * @param baiduUserId    The Baidu user ID.
     * @param baiduChannelId THe Baidu channel ID.
     */
    public BaiduRegistration(String baiduUserId, String baiduChannelId) {
        this(null, baiduUserId, baiduChannelId);
    }

    /**
     * Creates a Baidu registration with registration ID, Baidu user ID and channel
     * ID.
     *
     * @param registrationId The registration ID.
     * @param baiduUserId    The Baidu user ID.
     * @param baiduChannelId The Baidu channel ID.
     */
    public BaiduRegistration(String registrationId, String baiduUserId, String baiduChannelId) {
        super(registrationId);
        this.baiduUserId = baiduUserId;
        this.baiduChannelId = baiduChannelId;
    }

    /**
     * Gets the Baidu user ID.
     *
     * @return The Baidu user ID.
     */
    public String getBaiduUserId() {
        return baiduUserId;
    }

    /**
     * Sets the Baidu user ID.
     *
     * @param baiduUserId The Baidu user ID.
     */
    public void setBaiduUserId(String baiduUserId) {
        this.baiduUserId = baiduUserId;
    }

    /**
     * Gets the Baidu channel ID.
     *
     * @return The Baidu channel ID.
     */
    public String getBaiduChannelId() {
        return baiduChannelId;
    }

    /**
     * Sets the Baidu channel ID.
     *
     * @param baiduChannelId The Baidu channel ID.
     */
    public void setBaiduChannelId(String baiduChannelId) {
        this.baiduChannelId = baiduChannelId;
    }

    @Override
    public int hashCode() {
        String channel = (baiduUserId == null ? "" : baiduUserId) + "-"
                + (baiduChannelId == null ? "" : baiduChannelId);
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((baiduUserId == null && baiduChannelId == null) ? 0 : channel.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BaiduRegistration other = (BaiduRegistration) obj;
        return baiduUserId.equals(other.baiduUserId) && baiduChannelId.equals(other.baiduChannelId);
    }

    @Override
    public String getXml() {
        return BAIDU_NATIVE_REGISTRATION1 + getTagsXml() + BAIDU_NATIVE_REGISTRATION2 + baiduUserId
                + BAIDU_NATIVE_REGISTRATION3 + baiduChannelId + BAIDU_NATIVE_REGISTRATION4;
    }
}
