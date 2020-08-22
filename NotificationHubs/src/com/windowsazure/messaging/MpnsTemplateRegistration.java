//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a registration for template notifications for devices
 * using MPNS.
 */
public class MpnsTemplateRegistration extends MpnsRegistration {
    private static final String MPNS_TEMPLATE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><MpnsTemplateRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String MPNS_TEMPLATE_REGISTRATION2 = "<ChannelUri>";
    private static final String MPNS_TEMPLATE_REGISTRATION3 = "</ChannelUri><BodyTemplate><![CDATA[";
    private static final String MPNS_TEMPLATE_REGISTRATION4 = "]]></BodyTemplate>";
    private static final String MPNS_TEMPLATE_REGISTRATION5 = "</MpnsTemplateRegistrationDescription></content></entry>";

    private String bodyTemplate;
    private Map<String, String> headers = new HashMap<>();

    /**
     * Creates a Windows Phone template registration.
     */
    public MpnsTemplateRegistration() {
    }

    /**
     * Creates a Windows Phone template registration with a channel URI, body
     * template and headers.
     *
     * @param channelUri   The channel URI.
     * @param bodyTemplate The template body.
     * @param headers      The headers for the registration.
     */
    public MpnsTemplateRegistration(URI channelUri, String bodyTemplate, Map<String, String> headers) {
        super(channelUri);
        this.bodyTemplate = bodyTemplate;
        this.headers = headers;
    }

    /**
     * Creates a Windows Phone template registration with a channel URI and template
     * body.
     *
     * @param channelUri   The Windows Phone channel URI.
     * @param bodyTemplate The template body.
     */
    public MpnsTemplateRegistration(URI channelUri, String bodyTemplate) {
        super(channelUri);
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
     * Sets the template body.
     *
     * @param bodyTemplate The template body.
     */
    public void setBodyTemplate(String bodyTemplate) {
        this.bodyTemplate = bodyTemplate;
    }

    /**
     * Gets the template registration headers.
     *
     * @return The template registration headers.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Adds a header to the Windows Phone template registration
     *
     * @param name  The name of the header to set.
     * @param value The value of the header to set.s
     */
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((bodyTemplate == null) ? 0 : bodyTemplate.hashCode());
        result = prime * result + ((headers == null) ? 0 : headers.hashCode());
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
        MpnsTemplateRegistration other = (MpnsTemplateRegistration) obj;
        if (bodyTemplate == null) {
            if (other.bodyTemplate != null)
                return false;
        } else if (!bodyTemplate.equals(other.bodyTemplate))
            return false;
        if (headers == null) {
            return other.headers == null;
        } else
            return headers.equals(other.headers);
    }

    @Override
    public String getXml() {
        return MPNS_TEMPLATE_REGISTRATION1 + getTagsXml() + MPNS_TEMPLATE_REGISTRATION2 + channelUri.toString()
                + MPNS_TEMPLATE_REGISTRATION3 + bodyTemplate + MPNS_TEMPLATE_REGISTRATION4 + getHeadersXml()
                + MPNS_TEMPLATE_REGISTRATION5;
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
