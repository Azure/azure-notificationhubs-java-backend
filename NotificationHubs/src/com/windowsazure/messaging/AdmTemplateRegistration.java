//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

/**
 * This class represents an Amazon device template registration.
 */
public class AdmTemplateRegistration extends AdmRegistration implements TemplateRegistration {
    private static final String ADM_TEMPLATE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><AdmTemplateRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String ADM_TEMPLATE_REGISTRATION2 = "<AdmRegistrationId>";
    private static final String ADM_TEMPLATE_REGISTRATION3 = "</AdmRegistrationId><BodyTemplate><![CDATA[";
    private static final String ADM_TEMPLATE_REGISTRATION4 = "]]></BodyTemplate></AdmTemplateRegistrationDescription></content></entry>";

    private String bodyTemplate;

    /**
     * Creates a new instance of the AdmTemplateRegistration class.
     */
    public AdmTemplateRegistration() {
        super();
    }

    /**
     * Creates a new instance of the AdmTemplateRegistration class.
     * @param registrationId The Azure Notification Hubs registration ID.
     * @param admRegistrationId The Amazon device registration ID.
     * @param bodyTemplate The template body.
     */
    public AdmTemplateRegistration(
        String registrationId,
        String admRegistrationId,
        String bodyTemplate
    ) {
        super(registrationId, admRegistrationId);
        this.bodyTemplate = bodyTemplate;
    }

    /**
     * Creates a new instance of the AdmTemplateRegistration class.
     * @param admRegistrationId The Amazon device registration ID.
     * @param bodyTemplate The template body.
     */
    public AdmTemplateRegistration(String admRegistrationId, String bodyTemplate) {
        super(admRegistrationId);
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
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
            + ((bodyTemplate == null) ? 0 : bodyTemplate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        AdmTemplateRegistration other = (AdmTemplateRegistration) obj;
        if (bodyTemplate == null) {
            if (other.bodyTemplate != null)
                return false;
        } else if (!bodyTemplate.equals(other.bodyTemplate))
            return false;
        return true;
    }

    @Override
    public String getXml() {
        return ADM_TEMPLATE_REGISTRATION1 +
            getTagsXml() +
            ADM_TEMPLATE_REGISTRATION2 +
            getAdmRegistrationId() +
            ADM_TEMPLATE_REGISTRATION3 +
            bodyTemplate +
            ADM_TEMPLATE_REGISTRATION4;
    }
}
