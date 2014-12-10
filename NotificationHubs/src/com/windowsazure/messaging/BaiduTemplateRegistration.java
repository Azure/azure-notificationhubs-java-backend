package com.windowsazure.messaging;

public class BaiduTemplateRegistration extends BaiduRegistration {
	private static final String BAIDU_NATIVE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><BaiduTemplateRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
	private static final String BAIDU_NATIVE_REGISTRATION2 = "<BaiduUserId>";
	private static final String BAIDU_NATIVE_REGISTRATION3 = "</BaiduUserId><BaiduChannelId>";
	private static final String BAIDU_NATIVE_REGISTRATION4 = "</BaiduChannelId><BodyTemplate><![CDATA[";
	private static final String BAIDU_NATIVE_REGISTRATION5 = "]]></BodyTemplate></BaiduTemplateRegistrationDescription></content></entry>";

	private String bodyTemplate;	
	
	public BaiduTemplateRegistration() {
		this(null, null, null);
	}
	
	public BaiduTemplateRegistration(String baiduUserId, String baiduChannelId, String bodyTemplate) {
		this(null, baiduUserId, baiduChannelId, bodyTemplate);
	}

	public BaiduTemplateRegistration(String registrationId, String baiduUserId, String baiduChannelId, String bodyTemplate) {
		super(registrationId, baiduUserId, baiduChannelId);
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
		BaiduTemplateRegistration other = (BaiduTemplateRegistration) obj;
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
		buf.append(BAIDU_NATIVE_REGISTRATION1);
		buf.append(getTagsXml());
		buf.append(BAIDU_NATIVE_REGISTRATION2);
		buf.append(baiduUserId);
		buf.append(BAIDU_NATIVE_REGISTRATION3);
		buf.append(baiduChannelId);
		buf.append(BAIDU_NATIVE_REGISTRATION4);
		buf.append(bodyTemplate);
		buf.append(BAIDU_NATIVE_REGISTRATION5);
		return buf.toString();
	}

}
