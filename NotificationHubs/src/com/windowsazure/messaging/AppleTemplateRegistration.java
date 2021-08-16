//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.Objects;

/**
 * Class representing a registration for template notifications for devices using APNs.
 */
public class AppleTemplateRegistration extends AppleRegistration implements TemplateRegistration {
    private static final String APNS_TEMPLATE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><AppleTemplateRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String APNS_TEMPLATE_REGISTRATION2 = "<DeviceToken>";
    private static final String APNS_TEMPLATE_REGISTRATION3 = "</DeviceToken><BodyTemplate><![CDATA[";
    private static final String APNS_TEMPLATE_REGISTRATION4 = "]]></BodyTemplate>";
    private static final String APNS_TEMPLATE_REGISTRATION5 = "</AppleTemplateRegistrationDescription></content></entry>";

    private String bodyTemplate;
    private String expiry;

    /**
     * Creates a new instance of the AppleTemplateRegistration class.
     */
    public AppleTemplateRegistration() {
        super();
    }

    /**
     * Creates a new instance of the AppleTemplateRegistration class.
     * @param registrationId The registration ID for the device.
     * @param deviceToken The APNS device token for the registration.
     * @param bodyTemplate The template body for the registration.
     */
    public AppleTemplateRegistration(
        String registrationId,
        String deviceToken,
        String bodyTemplate
    ) {
        super(registrationId, deviceToken);
        this.bodyTemplate = bodyTemplate;
    }

    /**
     * Creates a new instance of the AppleTemplateRegistration class.
     * @param deviceToken The APNS device token for the registration
     * @param bodyTemplate The template body for the registration.
     */
    public AppleTemplateRegistration(String deviceToken, String bodyTemplate) {
        super(deviceToken);
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

    /**
     * Gets the expiration for the template registration.
     * @return The expiration for the template registration.
     */
    public String getExpiry() { return expiry; }

    /**
     * Sets the expiration for the template registration.
     * @param value The expiration for the template registration to set.
     */
    public void setExpiry(String value) { expiry = value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AppleTemplateRegistration that = (AppleTemplateRegistration) o;
        return Objects.equals(getBodyTemplate(), that.getBodyTemplate()) && Objects.equals(getExpiry(), that.getExpiry());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBodyTemplate(), getExpiry());
    }

    @Override
    public String getXml() {
        return APNS_TEMPLATE_REGISTRATION1 +
            getTagsXml() +
            APNS_TEMPLATE_REGISTRATION2 +
            deviceToken +
            APNS_TEMPLATE_REGISTRATION3 +
            bodyTemplate +
            APNS_TEMPLATE_REGISTRATION4 +
            getExpiryXml() +
            APNS_TEMPLATE_REGISTRATION5;
    }

    private String getExpiryXml() {
        if (expiry == null) return "";
        return "<Expiry>" + expiry + "</Expiry>";
    }

}
