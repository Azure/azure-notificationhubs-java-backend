//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.Objects;

/**
 * This class represents a Firebase Messaging V1 device template registration.
 */
public class FcmV1TemplateRegistration extends FcmV1Registration implements TemplateRegistration {
    private static final String FCM_V1_TEMPLATE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><FcmV1TemplateRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String FCM_V1_TEMPLATE_REGISTRATION2 = "<FcmV1RegistrationId>";
    private static final String FCM_V1_TEMPLATE_REGISTRATION3 = "</FcmV1RegistrationId><BodyTemplate><![CDATA[";
    private static final String FCM_V1_TEMPLATE_REGISTRATION4 = "]]></BodyTemplate></FcmV1TemplateRegistrationDescription></content></entry>";

    private String bodyTemplate;

    /**
     * Creates a new instance of the FcmV1TemplateRegistration class.
     */
    public FcmV1TemplateRegistration() {
        super();
    }

    /**
     * Creates a new instance of the FcmV1TemplateRegistration class.
     * @param registrationId The Azure Notification Hubs registration ID.
     * @param fcmRegistrationId The Firebase Messaging registration ID.
     * @param bodyTemplate The registration template body.
     */
    public FcmV1TemplateRegistration(
        String registrationId,
        String fcmRegistrationId,
        String bodyTemplate
    ) {
        super(registrationId, fcmRegistrationId);
        this.bodyTemplate = bodyTemplate;
    }

    /**
     * Creates a new instance of the FcmV1TemplateRegistration class.
     * @param fcmRegistrationId The Firebase Messaging registration ID.
     * @param bodyTemplate The registration template body.
     */
    public FcmV1TemplateRegistration(
        String fcmRegistrationId,
        String bodyTemplate
    ) {
        super(fcmRegistrationId);
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
        FcmV1TemplateRegistration that = (FcmV1TemplateRegistration) o;
        return Objects.equals(getBodyTemplate(), that.getBodyTemplate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBodyTemplate());
    }

    @Override
    public String getXml() {
        return FCM_V1_TEMPLATE_REGISTRATION1 +
            getTagsXml() +
            FCM_V1_TEMPLATE_REGISTRATION2 +
            fcmRegistrationId +
            FCM_V1_TEMPLATE_REGISTRATION3 +
            bodyTemplate +
            FCM_V1_TEMPLATE_REGISTRATION4;
    }
}
