//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.Objects;

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
     * Creates a new instance of the BaiduRegistration class.
     */
    public BaiduRegistration() {
        super();
    }

    /**
     * Creates a new instance of the BaiduRegistration class.
     * @param baiduUserId The Baidu user ID for the device.
     * @param baiduChannelId The Baidu channel ID for the device.
     */
    public BaiduRegistration(String baiduUserId, String baiduChannelId) {
        super();
        this.baiduUserId = baiduUserId;
        this.baiduChannelId = baiduChannelId;
    }

    /**
     * Creates a new instance of the BaiduRegistration class.
     * @param registrationId The Azure Notification Hubs registration ID.
     * @param baiduUserId The Baidu user ID for the device.
     * @param baiduChannelId The Baidu channel ID for the device.
     */
    public BaiduRegistration(String registrationId, String baiduUserId, String baiduChannelId) {
        super(registrationId);
        this.baiduUserId = baiduUserId;
        this.baiduChannelId = baiduChannelId;
    }

    /**
     * Gets the Baidu user ID for the device.
     * @return The Baidu user ID for the device.
     */
    public String getBaiduUserId() { return baiduUserId; }

    /**
     * Sets the Baidu user ID for the device.
     * @param value The Baidu user ID for the device to set.
     */
    public void setBaiduUserId(String value) { baiduUserId = value; }

    /**
     * Gets the Baidu channel ID for the device.
     * @return The Baidu channel ID for the device.
     */
    public String getBaiduChannelId() { return baiduChannelId; }

    /**
     * Sets the Baidu channel ID for the device.
     * @param value The Baidu channel ID for the device to set.
     */
    public void setBaiduChannelId(String value) { baiduChannelId = value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BaiduRegistration that = (BaiduRegistration) o;
        return Objects.equals(getBaiduUserId(), that.getBaiduUserId()) && Objects.equals(getBaiduChannelId(), that.getBaiduChannelId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBaiduUserId(), getBaiduChannelId());
    }

    @Override
    public String getXml() {
        return BAIDU_NATIVE_REGISTRATION1 +
            getTagsXml() +
            BAIDU_NATIVE_REGISTRATION2 +
            baiduUserId +
            BAIDU_NATIVE_REGISTRATION3 +
            baiduChannelId +
            BAIDU_NATIVE_REGISTRATION4;
    }
}
