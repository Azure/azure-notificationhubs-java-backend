//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class representing a registration for template notifications for devices using WNS.
 */
public class WindowsTemplateRegistration extends WindowsRegistration implements TemplateRegistration {
    private static final String WNS_TEMPLATE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><WindowsTemplateRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String WNS_TEMPLATE_REGISTRATION2 = "<ChannelUri>";
    private static final String WNS_TEMPLATE_REGISTRATION3 = "</ChannelUri><BodyTemplate><![CDATA[";
    private static final String WNS_TEMPLATE_REGISTRATION4 = "]]></BodyTemplate>";
    private static final String WNS_TEMPLATE_REGISTRATION5 = "</WindowsTemplateRegistrationDescription></content></entry>";

    private String bodyTemplate;
    private Map<String, String> headers = new HashMap<String, String>();

    /**
     * Creates a new instance of the WindowsTemplateRegistration class.
     */
    public WindowsTemplateRegistration() {
        super();
    }

    /**
     * Creates a new instance of the WindowsTemplateRegistration class.
     * @param channelUri The WNS channel URI
     * @param bodyTemplate The Windows registration template body.
     */
    public WindowsTemplateRegistration(
        URI channelUri,
        String bodyTemplate
    ) {
        super(channelUri);
        this.bodyTemplate = bodyTemplate;
    }

    /**
     * Creates a new instance of the WindowsTemplateRegistration class.
     * @param registrationId The Azure Notification Hubs registration ID.
     * @param channelUri The WNS channel URI.
     * @param bodyTemplate The Windows registration template body.
     */
    public WindowsTemplateRegistration(
        String registrationId,
        URI channelUri,
        String bodyTemplate
    ) {
        super(registrationId, channelUri);
        this.bodyTemplate = bodyTemplate;
    }

    /**
     * Creates a new instance of the WindowsTemplateRegistration class.
     * @param channelUri The WNS channel URI.
     * @param bodyTemplate The Windows registration template body.
     * @param headers The WNS headers for the template registration.
     */
    public WindowsTemplateRegistration(
        URI channelUri,
        String bodyTemplate,
        Map<String, String> headers
    ) {
        super(channelUri);
        this.bodyTemplate = bodyTemplate;
        this.headers = headers;
    }

    /**
     * Creates a new instance of the WindowsTemplateRegistration class.
     * @param registrationId The Azure Notification Hubs registration ID.
     * @param channelUri The WNS channel URI.
     * @param bodyTemplate The Windows registration template body.
     * @param headers The WNS headers for the template registration.
     */
    public WindowsTemplateRegistration(
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
     * Gets the WNS headers for the template registration.
     * @return The WNS headers for the template registration.
     */
    public Map<String, String> getHeaders() { return headers; }

    /**
     * Adds a header to the WNS headers.
     * @param name The name of the WNS header to add.
     * @param value The value for the WNS header to add.
     */
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    /**
     * Removes a header from the WNS headers.
     * @param name The name of the WNS header to remove.
     */
    public void removeHeader(String name) { headers.remove(name); }

    /**
     * Clears the WNS headers.
     */
    public void clearHeaders() { headers.clear(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WindowsTemplateRegistration that = (WindowsTemplateRegistration) o;
        return Objects.equals(getBodyTemplate(), that.getBodyTemplate()) && Objects.equals(getHeaders(), that.getHeaders());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBodyTemplate(), getHeaders());
    }

    @Override
    public String getXml() {
        return WNS_TEMPLATE_REGISTRATION1 +
            getTagsXml() +
            WNS_TEMPLATE_REGISTRATION2 +
            channelUri.toString() +
            WNS_TEMPLATE_REGISTRATION3 +
            bodyTemplate +
            WNS_TEMPLATE_REGISTRATION4 +
            getHeadersXml() +
            WNS_TEMPLATE_REGISTRATION5;
    }

    private String getHeadersXml() {
        StringBuilder buf = new StringBuilder();
        if (!headers.isEmpty()) {
            buf.append("<WnsHeaders>");
            for (String key : headers.keySet()) {
                buf.append("<WnsHeader><Header>");
                buf.append(key).append("</Header><Value>");
                buf.append(headers.get(key)).append("</Value></WnsHeader>");
            }
        }
        buf.append("</WnsHeaders>");
        return buf.toString();
    }
}
