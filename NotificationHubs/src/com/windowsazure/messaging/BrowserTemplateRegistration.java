//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.Objects;

/**
 * This class represents a browser push device template registration.
 */
public class BrowserTemplateRegistration extends BrowserRegistration implements TemplateRegistration {
    private static final String BROWSER_TEMPLATE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><BrowserTemplateRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String BROWSER_TEMPLATE_REGISTRATION2 = "<Endpoint>";
    private static final String BROWSER_TEMPLATE_REGISTRATION3 = "</Endpoint><P256DH>";
    private static final String BROWSER_TEMPLATE_REGISTRATION4 = "</P256DH><Auth>";
    private static final String BROWSER_TEMPLATE_REGISTRATION5 = "</Auth><BodyTemplate><![CDATA[";
    private static final String BROWSER_TEMPLATE_REGISTRATION6 = "]]></BodyTemplate></BrowserTemplateRegistrationDescription></content></entry>";

    private String bodyTemplate;

    /**
     * Creates a new instance of the BrowserTemplateRegistration class.
     */
    public BrowserTemplateRegistration() {
        super();
    }

    /**
     * Creates a new instance of the BrowserTemplateRegistration class.
     * @param registrationId The Azure Notification Hubs registration ID.
     * @param browserPushSubscription The browser push subscription.
     * @param bodyTemplate The registration template body.
     */
    public BrowserTemplateRegistration(
        String registrationId,
        BrowserPushSubscription browserPushSubscription,
        String bodyTemplate
    ) {
        super(registrationId, browserPushSubscription);
        this.bodyTemplate = bodyTemplate;
    }

    /**
     * Creates a new instance of the BrowserTemplateRegistration class.
     * @param browserPushSubscription The browser push subscription.
     * @param bodyTemplate The registration template body.
     */
    public BrowserTemplateRegistration(
        BrowserPushSubscription browserPushSubscription,
        String bodyTemplate
    ) {
        super(browserPushSubscription);
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
        BrowserTemplateRegistration that = (BrowserTemplateRegistration) o;
        return Objects.equals(getBodyTemplate(), that.getBodyTemplate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBodyTemplate());
    }

    @Override
    public String getXml() {
        return BROWSER_TEMPLATE_REGISTRATION1 +
            getTagsXml() +
            BROWSER_TEMPLATE_REGISTRATION2 +
            browserPushSubscription.getEndpoint() +
            BROWSER_TEMPLATE_REGISTRATION3 +
            browserPushSubscription.getP256dh() +
            BROWSER_TEMPLATE_REGISTRATION4 +
            browserPushSubscription.getAuth() +
            BROWSER_TEMPLATE_REGISTRATION5 +
            bodyTemplate +
            BROWSER_TEMPLATE_REGISTRATION6;
    }
}
