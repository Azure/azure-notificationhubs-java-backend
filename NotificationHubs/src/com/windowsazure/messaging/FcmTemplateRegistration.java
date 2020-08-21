//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

/**
 * A class which represents a Firebase Cloud Messaging Template Registration
 */
public class FcmTemplateRegistration extends FcmRegistration {
	// TODO replace content to FCM* when new version of backend will be released
	private static final String FCM_TEMPLATE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><GcmTemplateRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
	private static final String FCM_TEMPLATE_REGISTRATION2 = "<GcmRegistrationId>";
	private static final String FCM_TEMPLATE_REGISTRATION3 = "</GcmRegistrationId><BodyTemplate><![CDATA[";
	private static final String FCM_TEMPLATE_REGISTRATION4 = "]]></BodyTemplate></GcmTemplateRegistrationDescription></content></entry>";

	private String bodyTemplate;

	/**
	 * Creates an FCM template registration.
	 */
	public FcmTemplateRegistration() {
		super();
	}

	/**
	 * Creates an FCM template registration with registration ID, FCM registration ID and body template.
	 * @param registrationId The registration ID.
	 * @param fcmRegistrationId The FCM registration ID.
	 * @param bodyTemplate The body template.
	 */
	public FcmTemplateRegistration(String registrationId,
								   String fcmRegistrationId, String bodyTemplate) {
		super(registrationId, fcmRegistrationId);
		this.bodyTemplate = bodyTemplate;
	}

	/**
	 * Creates an FCM template registration with FCM registration ID and body template.
	 * @param fcmRegistrationId The FCM registration ID.
	 * @param bodyTemplate The body template.
	 */
	public FcmTemplateRegistration(String fcmRegistrationId, String bodyTemplate) {
		super(fcmRegistrationId);
		this.bodyTemplate = bodyTemplate;
	}

	/**
	 * Gets the body template.
	 * @return The body template.
	 */
	public String getBodyTemplate() {
		return bodyTemplate;
	}

	/**
	 * Sets the body template.
	 * @param bodyTemplate The body template to set.
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
		FcmTemplateRegistration other = (FcmTemplateRegistration) obj;
		if (bodyTemplate == null) {
			return other.bodyTemplate == null;
		} else return bodyTemplate.equals(other.bodyTemplate);
	}

	@Override
	public String getXml() {
		return FCM_TEMPLATE_REGISTRATION1 +
				getTagsXml() +
				FCM_TEMPLATE_REGISTRATION2 +
				fcmRegistrationId +
				FCM_TEMPLATE_REGISTRATION3 +
				bodyTemplate +
				FCM_TEMPLATE_REGISTRATION4;
	}
}