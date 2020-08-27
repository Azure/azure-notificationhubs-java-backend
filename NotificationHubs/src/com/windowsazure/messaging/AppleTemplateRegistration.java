//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a registration for template notifications for devices
 * using APNs.
 */
public class AppleTemplateRegistration extends AppleRegistration {
    private static final String APNS_TEMPLATE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><AppleTemplateRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
    private static final String APNS_TEMPLATE_REGISTRATION2 = "<DeviceToken>";
    private static final String APNS_TEMPLATE_REGISTRATION3 = "</DeviceToken><BodyTemplate><![CDATA[";
    private static final String APNS_TEMPLATE_REGISTRATION4 = "]]></BodyTemplate>";
    private static final String APNS_TEMPLATE_REGISTRATION5 = "</AppleTemplateRegistrationDescription></content></entry>";

    private String bodyTemplate;
    private String expiry;
    private Map<String, String> headers = new HashMap<>();

    /**
     * Creates an Apple template registration.
     */
    public AppleTemplateRegistration() {
        super();
    }

    /**
     * Creates an Apple registration with registration ID, device token and body
     * template.
     *
     * @param registrationId The registration ID.
     * @param deviceToken    The APNS device token.
     * @param bodyTemplate   The body template.
     */
    public AppleTemplateRegistration(String registrationId, String deviceToken, String bodyTemplate) {
        super(registrationId, deviceToken);
        this.bodyTemplate = bodyTemplate;
    }

    /**
     * Creates an Apple template registration with device token and body template.
     *
     * @param deviceToken  The APNS device token.
     * @param bodyTemplate The body template.
     */
    public AppleTemplateRegistration(String deviceToken, String bodyTemplate) {
        super(deviceToken);
        this.bodyTemplate = bodyTemplate;
    }

    /**
     * Creates an Apple template registration with device token, body template and
     * headers.
     *
     * @param deviceToken  The APNS device token.
     * @param bodyTemplate The body template.
     * @param headers      The APNS headers.
     */
    public AppleTemplateRegistration(String deviceToken, String bodyTemplate, Map<String, String> headers) {
        super(deviceToken);
        this.bodyTemplate = bodyTemplate;
        this.headers = headers;
    }

    /**
     * Gets the body template.
     *
     * @return The body template.
     */
    public String getBodyTemplate() {
        return bodyTemplate;
    }

    /**
     * Sets the body template.
     *
     * @param bodyTemplate The body template.
     */
    public void setBodyTemplate(String bodyTemplate) {
        this.bodyTemplate = bodyTemplate;
    }

    /**
     * Gets the expiration for the template registration.
     *
     * @return The expiration for the template registration.
     */
    public String getExpiry() {
        return expiry;
    }

    /**
     * Sets the expiration for the template registration.
     *
     * @param expiry The expiration for the template registration.
     */
    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    /**
     * Gets the headers for the template registration.
     *
     * @return The headers for the template registration.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Adds a header to the template registration headers.
     *
     * @param name  The header name to add.
     * @param value The header value to add.
     */
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    /**
     * Removes a header from the template registration.
     *
     * @param name The header name to remove.
     */
    public void removeHeader(String name) {
        headers.remove(name);
    }

    /**
     * Clears the template registration headers.
     */
    public void clearHeaders() {
        headers.clear();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((bodyTemplate == null) ? 0 : bodyTemplate.hashCode());
        result = prime * result + ((expiry == null) ? 0 : expiry.hashCode());
        result = prime * result + ((headers == null) ? 0 : headers.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AppleTemplateRegistration other = (AppleTemplateRegistration) obj;
        if (bodyTemplate == null) {
            if (other.bodyTemplate != null) {
                return false;
            }
        } else if (!bodyTemplate.equals(other.bodyTemplate)) {
            return false;
        }
        if (expiry == null) {
            if (other.expiry != null) {
                return false;
            }
        } else if (!expiry.equals(other.expiry)) {
            return false;
        }
        if (headers == null) {
            return other.headers == null;
        } else {
            return headers.equals(other.headers);
        }
    }

    @Override
    public String getXml() {
        return APNS_TEMPLATE_REGISTRATION1 + getTagsXml() + APNS_TEMPLATE_REGISTRATION2 + deviceToken
            + APNS_TEMPLATE_REGISTRATION3 + bodyTemplate + APNS_TEMPLATE_REGISTRATION4 + getExpiryXml()
            + getHeadersXml() + APNS_TEMPLATE_REGISTRATION5;
    }

    private String getHeadersXml() {
        StringBuilder buf = new StringBuilder();
        if (!headers.isEmpty()) {
            buf.append("<ApnsHeaders>");
            for (String key : headers.keySet()) {
                buf.append("<ApnsHeader><Header>");
                buf.append(key).append("</Header><Value>");
                buf.append(headers.get(key)).append("</Value></ApnsHeader>");
            }
        }
        buf.append("</ApnsHeaders>");
        return buf.toString();
    }

    private String getExpiryXml() {
        if (expiry == null)
            return "";
        return "<Expiry>" + expiry + "</Expiry>";
    }
}
