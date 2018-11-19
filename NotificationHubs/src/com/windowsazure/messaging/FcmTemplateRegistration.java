package com.windowsazure.messaging;

public class FcmTemplateRegistration extends FcmRegistration {
	private static final String FCM_TEMPLATE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><FcmTemplateRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
	private static final String FCM_TEMPLATE_REGISTRATION2 = "<FcmRegistrationId>";
	private static final String FCM_TEMPLATE_REGISTRATION3 = "</FcmRegistrationId><BodyTemplate><![CDATA[";
	private static final String FCM_TEMPLATE_REGISTRATION4 = "]]></BodyTemplate></FcmTemplateRegistrationDescription></content></entry>";

	private String bodyTemplate;

	public FcmTemplateRegistration() {
		super();
	}

	public FcmTemplateRegistration(String registrationId,
			String fcmRegistrationId, String bodyTemplate) {
		super(registrationId, fcmRegistrationId);
		this.bodyTemplate = bodyTemplate;
	}

	public FcmTemplateRegistration(String fcmRegistrationId, String bodyTemplate) {
		super(fcmRegistrationId);
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
		FcmTemplateRegistration other = (FcmTemplateRegistration) obj;
		if (bodyTemplate == null) {
			if (other.bodyTemplate != null)
				return false;
		} else if (!bodyTemplate.equals(other.bodyTemplate))
			return false;
		return true;
	}

	@Override
	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append(FCM_TEMPLATE_REGISTRATION1);
		buf.append(getTagsXml());
		buf.append(FCM_TEMPLATE_REGISTRATION2);
		buf.append(fcmRegistrationId);
		buf.append(FCM_TEMPLATE_REGISTRATION3);
		buf.append(bodyTemplate);
		buf.append(FCM_TEMPLATE_REGISTRATION4);
		return buf.toString();
	}
}
