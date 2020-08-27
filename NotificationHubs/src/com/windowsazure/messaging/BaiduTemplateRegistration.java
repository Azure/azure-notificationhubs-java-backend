//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

/**
 * This class represents a Baidu template registration.
 */
public class BaiduTemplateRegistration extends BaiduRegistration {
    private static final String BAIDU_NATIVE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><BaiduTemplateRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String BAIDU_NATIVE_REGISTRATION2 = "<BaiduUserId>";
    private static final String BAIDU_NATIVE_REGISTRATION3 = "</BaiduUserId><BaiduChannelId>";
    private static final String BAIDU_NATIVE_REGISTRATION4 = "</BaiduChannelId><BodyTemplate><![CDATA[";
    private static final String BAIDU_NATIVE_REGISTRATION5 = "]]></BodyTemplate></BaiduTemplateRegistrationDescription></content></entry>";

    private String bodyTemplate;

    /**
     * Creates a Baidu template registration.
     */
    public BaiduTemplateRegistration() {
        this(null, null, null);
    }

    /**
     * Creates a Baidu template registration with Baidu user ID, channel ID and body
     * template.
     *
     * @param baiduUserId    The Baidu user ID.
     * @param baiduChannelId The Baidu channel ID.
     * @param bodyTemplate   The template body.
     */
    public BaiduTemplateRegistration(String baiduUserId, String baiduChannelId, String bodyTemplate) {
        this(null, baiduUserId, baiduChannelId, bodyTemplate);
    }

    /**
     * Creates a Baidu template registration with registration ID, Baidu user ID and
     * channel ID, and template body.
     *
     * @param registrationId The registration ID.
     * @param baiduUserId    The Baidu user ID.
     * @param baiduChannelId The Baidu channel ID.
     * @param bodyTemplate   The template body.
     */
    public BaiduTemplateRegistration(String registrationId, String baiduUserId, String baiduChannelId,
            String bodyTemplate) {
        super(registrationId, baiduUserId, baiduChannelId);
        this.bodyTemplate = bodyTemplate;
    }

    /**
     * Gets the template body.
     *
     * @return The template body.
     */
    public String getBodyTemplate() {
        return bodyTemplate;
    }

    /**
     * Sets the body template.
     *
     * @param bodyTemplate The body template.
     */
    public void setBodyTemplate(String bodyTemplate) {
        this.bodyTemplate = bodyTemplate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((bodyTemplate == null) ? 0 : bodyTemplate.hashCode());
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
        BaiduTemplateRegistration other = (BaiduTemplateRegistration) obj;
        if (bodyTemplate == null) {
            return other.bodyTemplate == null;
        } else {
            return bodyTemplate.equals(other.bodyTemplate);
        }
    }

    @Override
    public String getXml() {
        return BAIDU_NATIVE_REGISTRATION1 + getTagsXml() + BAIDU_NATIVE_REGISTRATION2 + baiduUserId
                + BAIDU_NATIVE_REGISTRATION3 + baiduChannelId + BAIDU_NATIVE_REGISTRATION4 + bodyTemplate
                + BAIDU_NATIVE_REGISTRATION5;
    }
}
