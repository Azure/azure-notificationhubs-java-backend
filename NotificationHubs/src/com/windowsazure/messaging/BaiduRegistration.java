package com.windowsazure.messaging;

/**
 * Class representing a native registration for devices using Baidu PNS.
 *
 */

public class BaiduRegistration extends Registration {
	private static final String BAIDU_NATIVE_REGISTRATION1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><entry xmlns=\"http://www.w3.org/2005/Atom\"><content type=\"application/xml\"><BaiduRegistrationDescription xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.microsoft.com/netservices/2010/10/servicebus/connect\">";
	private static final String BAIDU_NATIVE_REGISTRATION2 = "<BaiduUserId>";
	private static final String BAIDU_NATIVE_REGISTRATION3 = "</BaiduUserId><BaiduChannelId>";
	private static final String BAIDU_NATIVE_REGISTRATION4 = "</BaiduChannelId></BaiduRegistrationDescription></content></entry>";

	protected String baiduUserId;
	protected String baiduChannelId;	
	
	public BaiduRegistration() {
		this(null, null);
	}
	
	public BaiduRegistration(String baiduUserId, String baiduChannelId) {
		this(null, baiduUserId, baiduChannelId);
	}

	public BaiduRegistration(String registrationId, String baiduUserId, String baiduChannelId) {
		super(registrationId);
		this.baiduUserId = baiduUserId;
		this.baiduChannelId = baiduChannelId;
	}	

	public String getBaiduUserId() {
		return baiduUserId;
	}

	public void setBaiduUserId(String baiduUserId) {
		this.baiduUserId = baiduUserId;
	}
	
	public String getBaiduChannelId() {
		return baiduChannelId;
	}

	public void setBaiduChannelId(String baiduChannelId) {
		this.baiduChannelId = baiduChannelId;
	}

	@Override
	public int hashCode() {
		String channel = (baiduUserId == null ? "" : baiduUserId) + "-" + (baiduChannelId == null ? "" : baiduChannelId);		
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((baiduUserId == null && baiduChannelId == null)  ? 0 : channel.hashCode());
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
		
		BaiduRegistration other = (BaiduRegistration) obj;
		return baiduUserId.equals(other.baiduUserId) && baiduChannelId.equals(other.baiduChannelId);
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
		return buf.toString();
	}

}
