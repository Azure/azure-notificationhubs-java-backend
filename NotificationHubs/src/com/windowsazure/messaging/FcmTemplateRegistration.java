//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.Objects;

/**
 * This class represents a Firebase Messaging device template registration.
 */
public class FcmTemplateRegistration extends FcmRegistration implements TemplateRegistration {
    private static final String FCM_TEMPLATE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><GcmTemplateRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String FCM_TEMPLATE_REGISTRATION2 = "<GcmRegistrationId>";
    private static final String FCM_TEMPLATE_REGISTRATION3 = "</GcmRegistrationId><BodyTemplate><![CDATA[";
    private static final String FCM_TEMPLATE_REGISTRATION4 = "]]></BodyTemplate></GcmTemplateRegistrationDescription></content></entry>";

    private String bodyTemplate;

    /**
     * Creates a new instance of the FcmTemplateRegistration class.
     */
    public FcmTemplateRegistration() {
        super();
    }

    /**
     * Creates a new instance of the FcmTemplateRegistration class.
     * @param registrationId The Azure Notification Hubs registration ID.
     * @param fcmRegistrationId The Firebase Messaging registration ID.
     * @param bodyTemplate The registration template body.
     */
    public FcmTemplateRegistration(
        String registrationId,
        String fcmRegistrationId,
        String bodyTemplate
    ) {
        super(registrationId, fcmRegistrationId);
        this.bodyTemplate = bodyTemplate;
    }

    /**
     * Creates a new instance of the FcmTemplateRegistration class.
     * @param fcmRegistrationId The Firebase Messaging registration ID.
     * @param bodyTemplate The registration template body.
     */
    public FcmTemplateRegistration(
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
        FcmTemplateRegistration that = (FcmTemplateRegistration) o;
        return Objects.equals(getBodyTemplate(), that.getBodyTemplate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBodyTemplate());
    }

    @Override
    public String getXml() {
        return FCM_TEMPLATE_REGISTRATION1 +
            getTagsXml() +
            FCM_TEMPLATE_REGISTRATION2 +
            fcmRegistrationId +
            FCM_TEMPLATE_REGISTRATION3 +
            bodyTemplate +
            FCM_TEMPLATE_REGISTRATION4;
    }
}
