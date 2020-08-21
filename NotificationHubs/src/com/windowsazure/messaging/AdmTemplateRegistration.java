//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

/**
 * This class represents the ADM template registration
 */
public class AdmTemplateRegistration extends AdmRegistration {
	private static final String ADM_TEMPLATE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><AdmTemplateRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
	private static final String ADM_TEMPLATE_REGISTRATION2 = "<AdmRegistrationId>";
	private static final String ADM_TEMPLATE_REGISTRATION3 = "</AdmRegistrationId><BodyTemplate><![CDATA[";
	private static final String ADM_TEMPLATE_REGISTRATION4 = "]]></BodyTemplate></AdmTemplateRegistrationDescription></content></entry>";

	private String bodyTemplate;

	/**
	 * Creates a new instance of the ADM template registration.
	 */
	public AdmTemplateRegistration() {
		super();
	}

	/**
	 * Creates a new instance of the ADM template registration with the registration ID, ADM registration ID and template body.
	 * @param registrationId The registration ID
	 * @param admRegistrationId The ADM registration ID.
	 * @param bodyTemplate The template body.
	 */
	public AdmTemplateRegistration(String registrationId, String admRegistrationId, String bodyTemplate) {
		super(registrationId, admRegistrationId);
		this.bodyTemplate = bodyTemplate;
	}

	/**
	 * Creates an ADM template registration with an ADM registration ID and template body.
	 * @param admRegistrationId The ADM registration ID.
	 * @param bodyTemplate The template body.
	 */
	public AdmTemplateRegistration(String admRegistrationId, String bodyTemplate) {
		super(admRegistrationId);
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
			return other.bodyTemplate == null;
		} else return bodyTemplate.equals(other.bodyTemplate);
	}

	@Override
	public String getXml() {
		return ADM_TEMPLATE_REGISTRATION1 +
				getTagsXml() +
				ADM_TEMPLATE_REGISTRATION2 +
				admRegistrationId +
				ADM_TEMPLATE_REGISTRATION3 +
				bodyTemplate +
				ADM_TEMPLATE_REGISTRATION4;
	}
}
