//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a registration for template notifications for devices using WNS.
 */
public class WindowsTemplateRegistration extends WindowsRegistration {
	private static final String WNS_TEMPLATE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><WindowsTemplateRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
	private static final String WNS_TEMPLATE_REGISTRATION2 = "<ChannelUri>";
	private static final String WNS_TEMPLATE_REGISTRATION3 = "</ChannelUri><BodyTemplate><![CDATA[";
	private static final String WNS_TEMPLATE_REGISTRATION4 = "]]></BodyTemplate>";
	private static final String WNS_TEMPLATE_REGISTRATION5 = "</WindowsTemplateRegistrationDescription></content></entry>";

	private String bodyTemplate;
	private Map<String, String> headers = new HashMap<>();

    /**
     * Creates a WNS template registration.
     */
	public WindowsTemplateRegistration() {
	}

    /**
     * Creates a WNS template registration with channel URI, body template and headers.
     * @param channelUri The channel URI for the WNS template registration.
     * @param bodyTemplate The template body for the WNS template registration.
     * @param headers The headers for the WNS template registration.
     */
	public WindowsTemplateRegistration(URI channelUri, String bodyTemplate,
			Map<String, String> headers) {
		super(channelUri);
		this.bodyTemplate = bodyTemplate;
		this.headers = headers;
	}

    /**
     * Creates a WNS template registration with channel URI and template body.
     * @param channelUri The channel URI for the WNS template registration.
     * @param bodyTemplate The template body for the WNS template registration.
     */
    public WindowsTemplateRegistration(URI channelUri, String bodyTemplate) {
        super(channelUri);
        this.bodyTemplate = bodyTemplate;
    }

    /**
     * Gets the template body.
     * @return The template body.
     */
	public String getBodyTemplate() {
		return bodyTemplate;
	}

    /**
     * Sets the template body.
     * @param bodyTemplate The template body.
     */
	public void setBodyTemplate(String bodyTemplate) {
		this.bodyTemplate = bodyTemplate;
	}

    /**
     * Gets the headers for the template registration.
     * @return The template registration headers.
     */
	public Map<String, String> getHeaders() {
		return headers;
	}

    /**
     * Adds a header to the template registration headers.
     * @param name The name of the header.
     * @param value The value of the header.
     */
	public void addHeader(String name, String value) {
		headers.put(name, value);
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
            + ((bodyTemplate == null) ? 0 : bodyTemplate.hashCode());
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
        WindowsTemplateRegistration other = (WindowsTemplateRegistration) obj;
        if (bodyTemplate == null) {
            if (other.bodyTemplate != null)
                return false;
        } else if (!bodyTemplate.equals(other.bodyTemplate))
            return false;
        if (headers == null) {
            return other.headers == null;
        } else return headers.equals(other.headers);
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
