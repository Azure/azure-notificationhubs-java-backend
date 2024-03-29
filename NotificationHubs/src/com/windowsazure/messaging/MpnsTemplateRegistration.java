//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class representing a registration for template notifications for devices using MPNS.
 */
public class MpnsTemplateRegistration extends MpnsRegistration implements TemplateRegistration {
    private static final String MPNS_TEMPLATE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><MpnsTemplateRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String MPNS_TEMPLATE_REGISTRATION2 = "<ChannelUri>";
    private static final String MPNS_TEMPLATE_REGISTRATION3 = "</ChannelUri><BodyTemplate><![CDATA[";
    private static final String MPNS_TEMPLATE_REGISTRATION4 = "]]></BodyTemplate>";
    private static final String MPNS_TEMPLATE_REGISTRATION5 = "</MpnsTemplateRegistrationDescription></content></entry>";

    private String bodyTemplate;
    private Map<String, String> headers = new HashMap<>();

    /**
     * Creates a new instance of the MpnsTemplateRegistration class.
     */
    public MpnsTemplateRegistration() {
    }

    /**
     * Creates a new instance of the MpnsTemplateRegistration class.
     * @param channelUri The Windows Phone PNS channel URI.
     * @param bodyTemplate The registration template body.
     * @param headers The Windows Phone PNS headers.
     */
    public MpnsTemplateRegistration(
        URI channelUri,
        String bodyTemplate,
        Map<String, String> headers
    ) {
        super(channelUri);
        this.bodyTemplate = bodyTemplate;
        this.headers = headers;
    }

    /**
     * Creates a new instance of the MpnsTemplateRegistration class.
     * @param registrationId The Azure Notification Hubs registration ID.
     * @param channelUri The Windows Phone PNS channel URI.
     * @param bodyTemplate The registration template body.
     * @param headers The Windows Phone PNS headers.
     */
    public MpnsTemplateRegistration(
        String registrationId,
        URI channelUri,
        String bodyTemplate,
        Map<String, String> headers
    ) {
        super(registrationId, channelUri);
        this.bodyTemplate = bodyTemplate;
        this.headers = headers;
    }

    /**
     * Creates a new instance of the MpnsTemplateRegistration class.
     * @param channelUri The Windows Phone PNS channel URI.
     * @param bodyTemplate The registration template body.
     */
    public MpnsTemplateRegistration(
        URI channelUri,
        String bodyTemplate
    ) {
        super(channelUri);
        this.bodyTemplate = bodyTemplate;
    }

    /**
     * Creates a new instance of the MpnsTemplateRegistration class.
     * @param registrationId The Azure Notification Hubs registration ID.
     * @param channelUri The Windows Phone PNS channel URI.
     * @param bodyTemplate The registration template body.
     */
    public MpnsTemplateRegistration(
        String registrationId,
        URI channelUri,
        String bodyTemplate
    ) {
        super(registrationId, channelUri);
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
     * Gets the Windows Phone PNS headers for the template registration.
     * @return The Windows Phone PNS headers for the template registration.
     */
    public Map<String, String> getHeaders() { return headers; }

    /**
     * Adds a header to the Windows Phone PNS headers for the template registration.
     * @param name The name of the header to add.
     * @param value The value of the header to add.
     */
    public void addHeader(String name, String value) { headers.put(name, value); }

    /**
     * Removes a header from the Windows Phone PNS headers for the template registration.
     * @param name
     */
    public void removeHeader(String name) { headers.remove(name); }

    /**
     * Clears the Windows Phone PNS headers for the template registration.
     */
    public void clearHeaders() { headers.clear(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MpnsTemplateRegistration that = (MpnsTemplateRegistration) o;
        return Objects.equals(getBodyTemplate(), that.getBodyTemplate()) && Objects.equals(getHeaders(), that.getHeaders());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBodyTemplate(), getHeaders());
    }

    @Override
    public String getXml() {
        return MPNS_TEMPLATE_REGISTRATION1 +
            getTagsXml() +
            MPNS_TEMPLATE_REGISTRATION2 +
            channelUri.toString() +
            MPNS_TEMPLATE_REGISTRATION3 +
            bodyTemplate +
            MPNS_TEMPLATE_REGISTRATION4 +
            getHeadersXml() +
            MPNS_TEMPLATE_REGISTRATION5;
    }

    private String getHeadersXml() {
        StringBuilder buf = new StringBuilder();
        if (!headers.isEmpty()) {
            buf.append("<MpnsHeaders>");
            for (String key : headers.keySet()) {
                buf.append("<MpnsHeader><Header>");
                buf.append(key).append("</Header><Value>");
                buf.append(headers.get(key)).append("</Value></MpnsHeader>");
            }
            buf.append("</MpnsHeaders>");
        }
        return buf.toString();
    }
}
