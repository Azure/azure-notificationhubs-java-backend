package com.windowsazure.messaging;

/**
 * This class represents a Browser Push device template registration.
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
     * @param endpoint The browser push registration endpoint URL.
     * @param p256dh The browser push registration P256DH.
     * @param auth The browser push registration auth secret.
     * @param bodyTemplate The browser push registration template body.
     */
    public BrowserTemplateRegistration(
        String registrationId,
        String endpoint,
        String p256dh,
        String auth,
        String bodyTemplate
    ) {
        super(registrationId, endpoint, p256dh, auth);
        this.bodyTemplate = bodyTemplate;
    }

    /**
     * Creates a new instance of the BrowserTemplateRegistration class.
     * @param endpoint The browser push registration endpoint URL.
     * @param p256dh The browser push registration P256DH.
     * @param auth The browser push registration auth secret.
     * @param bodyTemplate The browser push registration template body.
     */
    public BrowserTemplateRegistration(
        String endpoint,
        String p256dh,
        String auth,
        String bodyTemplate
    ) {
        super(endpoint, p256dh, auth);
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
    public String getXml() {
        return BROWSER_TEMPLATE_REGISTRATION1 +
            getTagsXml() +
            BROWSER_TEMPLATE_REGISTRATION2 +
            endpoint +
            BROWSER_TEMPLATE_REGISTRATION3 +
            p256dh +
            BROWSER_TEMPLATE_REGISTRATION4 +
            auth +
            BROWSER_TEMPLATE_REGISTRATION5 +
            bodyTemplate +
            BROWSER_TEMPLATE_REGISTRATION6;
    }
}
