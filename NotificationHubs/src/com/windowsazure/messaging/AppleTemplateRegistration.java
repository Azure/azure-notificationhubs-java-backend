//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a registration for template notifications for devices using APNs.
 *
 */
public class AppleTemplateRegistration extends AppleRegistration {
	private static final String APNS_TEMPLATE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><AppleTemplateRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
	private static final String APNS_TEMPLATE_REGISTRATION2 = "<DeviceToken>";
	private static final String APNS_TEMPLATE_REGISTRATION3 = "</DeviceToken><BodyTemplate><![CDATA[";
	private static final String APNS_TEMPLATE_REGISTRATION4 = "]]></BodyTemplate>";
	private static final String APNS_TEMPLATE_REGISTRATION5 = "</AppleTemplateRegistrationDescription></content></entry>";

	private String bodyTemplate;
	private String expiry;
	private Map<String, String> headers = new HashMap<String, String>();

	public AppleTemplateRegistration() {
		super();
	}

	public AppleTemplateRegistration(String registrationId, String deviceToken,
			String bodyTemplate) {
		super(registrationId, deviceToken);
		this.bodyTemplate = bodyTemplate;
	}

	public AppleTemplateRegistration(String deviceToken, String bodyTemplate) {
		super(deviceToken);
		this.bodyTemplate = bodyTemplate;
	}

	public AppleTemplateRegistration(String deviceToken, String bodyTemplate, Map<String, String> headers) {
		super(deviceToken);
		this.bodyTemplate = bodyTemplate;
		this.headers = headers;
	}

	public String getBodyTemplate() {
		return bodyTemplate;
	}

	public void setBodyTemplate(String bodyTemplate) {
		this.bodyTemplate = bodyTemplate;
	}

	public String getExpiry() {
		return expiry;
	}

	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void addHeader(String name, String value) {
		headers.put(name, value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result	+ ((bodyTemplate == null) ? 0 : bodyTemplate.hashCode());
		result = prime * result + ((expiry == null) ? 0 : expiry.hashCode());
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
		AppleTemplateRegistration other = (AppleTemplateRegistration) obj;
		if (bodyTemplate == null) {
			if (other.bodyTemplate != null)
				return false;
		} else if (!bodyTemplate.equals(other.bodyTemplate))
			return false;
		if (expiry == null) {
			if (other.expiry != null)
				return false;
		} else if (!expiry.equals(other.expiry))
			return false;
		if (headers == null) {
				if (other.headers != null)
					return false;
			} else if (!headers.equals(other.headers))
				return false;
		return true;
	}

	@Override
	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append(APNS_TEMPLATE_REGISTRATION1);
		buf.append(getTagsXml());
		buf.append(APNS_TEMPLATE_REGISTRATION2);
		buf.append(deviceToken);
		buf.append(APNS_TEMPLATE_REGISTRATION3);
		buf.append(bodyTemplate);
		buf.append(APNS_TEMPLATE_REGISTRATION4);
		buf.append(getExpiryXml());
		buf.append(getHeadersXml());
		buf.append(APNS_TEMPLATE_REGISTRATION5);
		return buf.toString();
	}

	private String getHeadersXml() {
		StringBuffer buf = new StringBuffer();
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
		if (expiry == null) return "";
		return "<Expiry>" + expiry + "</Expiry>";
	}
}
