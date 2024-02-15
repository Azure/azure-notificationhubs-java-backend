//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.Objects;

/**
 * @deprecated use {@link com.windowsazure.messaging.FcmV1TemplateRegistration#FcmV1TemplateRegistration()} instead.
 */
@Deprecated
public class GcmTemplateRegistration extends GcmRegistration implements TemplateRegistration {
    private static final String GCM_TEMPLATE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><GcmTemplateRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String GCM_TEMPLATE_REGISTRATION2 = "<GcmRegistrationId>";
    private static final String GCM_TEMPLATE_REGISTRATION3 = "</GcmRegistrationId><BodyTemplate><![CDATA[";
    private static final String GCM_TEMPLATE_REGISTRATION4 = "]]></BodyTemplate></GcmTemplateRegistrationDescription></content></entry>";

    private String bodyTemplate;

    /**
     * Creates a new instance of the GcmTemplateRegistration class.
     */
    public GcmTemplateRegistration() {
        super();
    }

    /**
     * Creates a new instance of the GcmTemplateRegistration class.
     * @param gcmRegistrationId The Google Cloud Messaging registration ID.
     * @param bodyTemplate The registration template body.
     */
    public GcmTemplateRegistration(String gcmRegistrationId, String bodyTemplate) {
        super(gcmRegistrationId);
        this.bodyTemplate = bodyTemplate;
    }

    /**
     * Creates a new instance of the GcmTemplateRegistration class.
     * @param registrationId The Azure Notification Hubs registration ID.
     * @param gcmRegistrationId The Google Cloud Messaging registration ID.
     * @param bodyTemplate The registration template body.
     */
    public GcmTemplateRegistration(
        String registrationId,
        String gcmRegistrationId,
        String bodyTemplate
    ) {
        super(registrationId, gcmRegistrationId);
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
        GcmTemplateRegistration that = (GcmTemplateRegistration) o;
        return Objects.equals(getBodyTemplate(), that.getBodyTemplate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBodyTemplate());
    }

    @Override
    public String getXml() {
        return GCM_TEMPLATE_REGISTRATION1 +
            getTagsXml() +
            GCM_TEMPLATE_REGISTRATION2 +
            gcmRegistrationId +
            GCM_TEMPLATE_REGISTRATION3 +
            bodyTemplate +
            GCM_TEMPLATE_REGISTRATION4;
    }
}
