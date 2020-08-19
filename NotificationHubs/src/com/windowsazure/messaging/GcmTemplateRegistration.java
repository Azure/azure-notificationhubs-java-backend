//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

/**
 * @deprecated use {@link com.windowsazure.messaging.FcmTemplateRegistration#FcmTemplateRegistration()} instead.
 */

@Deprecated
public class GcmTemplateRegistration extends GcmRegistration {
	private static final String GCM_TEMPLATE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><GcmTemplateRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
	private static final String GCM_TEMPLATE_REGISTRATION2 = "<GcmRegistrationId>";
	private static final String GCM_TEMPLATE_REGISTRATION3 = "</GcmRegistrationId><BodyTemplate><![CDATA[";
	private static final String GCM_TEMPLATE_REGISTRATION4 = "]]></BodyTemplate></GcmTemplateRegistrationDescription></content></entry>";

	private String bodyTemplate;

	public GcmTemplateRegistration() {
		super();
	}

	public GcmTemplateRegistration(String registrationId,
			String gcmRegistrationId, String bodyTemplate) {
		super(registrationId, gcmRegistrationId);
		this.bodyTemplate = bodyTemplate;
	}

	public GcmTemplateRegistration(String gcmRegistrationId, String bodyTemplate) {
		super(gcmRegistrationId);
		this.bodyTemplate = bodyTemplate;
	}

	public String getBodyTemplate() {
		return bodyTemplate;
	}

	public void setBodyTemplate(String bodyTemplate) {
		this.bodyTemplate = bodyTemplate;
	}



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
		GcmTemplateRegistration other = (GcmTemplateRegistration) obj;
		if (bodyTemplate == null) {
            return other.bodyTemplate == null;
		} else return bodyTemplate.equals(other.bodyTemplate);
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
