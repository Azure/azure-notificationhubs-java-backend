//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.Objects;

/**
 * Class representing a template registration for devices using Baidu PNS.
 */
public class BaiduTemplateRegistration extends BaiduRegistration implements TemplateRegistration {
    private static final String BAIDU_NATIVE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><BaiduTemplateRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String BAIDU_NATIVE_REGISTRATION2 = "<BaiduUserId>";
    private static final String BAIDU_NATIVE_REGISTRATION3 = "</BaiduUserId><BaiduChannelId>";
    private static final String BAIDU_NATIVE_REGISTRATION4 = "</BaiduChannelId><BodyTemplate><![CDATA[";
    private static final String BAIDU_NATIVE_REGISTRATION5 = "]]></BodyTemplate></BaiduTemplateRegistrationDescription></content></entry>";

    private String bodyTemplate;

    /**
     * Creates a new instance of the BaiduTemplateRegistration class.
     */
    public BaiduTemplateRegistration() {
        super();
    }

    /**
     * Creates a new instance of the BaiduTemplateRegistration class.
     * @param baiduUserId The Baidu user ID for the device.
     * @param baiduChannelId The Baidu channel ID for the device.
     * @param bodyTemplate The browser push registration template body.
     */
    public BaiduTemplateRegistration(String baiduUserId, String baiduChannelId, String bodyTemplate) {
        super(baiduUserId, baiduChannelId);
        this.bodyTemplate = bodyTemplate;
    }

    /**
     * Creates a new instance of the BaiduTemplateRegistration class.
     * @param registrationId The Azure Notification Hubs registration ID.
     * @param baiduUserId The Baidu user ID for the device.
     * @param baiduChannelId The Baidu channel ID for the device.
     * @param bodyTemplate The browser push registration template body.
     */
    public BaiduTemplateRegistration(
        String registrationId,
        String baiduUserId,
        String baiduChannelId,
        String bodyTemplate
    ) {
        super(registrationId, baiduUserId, baiduChannelId);
        this.bodyTemplate = bodyTemplate;
    }

    /**
     * Gets the registration template body.
     * @return The registration template body.
     */
    @Override
    public String getBodyTemplate() { return bodyTemplate; }

    /**
     * Sets the registration template body.
     * @param value The registration template body to set.
     */
    @Override
    public void setBodyTemplate(String value) { this.bodyTemplate = value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BaiduTemplateRegistration that = (BaiduTemplateRegistration) o;
        return Objects.equals(getBodyTemplate(), that.getBodyTemplate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBodyTemplate());
    }

    @Override
    public String getXml() {
        return BAIDU_NATIVE_REGISTRATION1 +
            getTagsXml() +
            BAIDU_NATIVE_REGISTRATION2 +
            baiduUserId +
            BAIDU_NATIVE_REGISTRATION3 +
            baiduChannelId +
            BAIDU_NATIVE_REGISTRATION4 +
            bodyTemplate +
            BAIDU_NATIVE_REGISTRATION5;
    }
}
