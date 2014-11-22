package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

public final class MpnsCredential extends PnsCredential {	
	private String mpnsCertificate;
	private String certificateKey;
	
	public MpnsCredential(){
		this(null,null);
	}
		
	public MpnsCredential(String mpnsCertificate, String certificateKey){
		super();
		this.setMpnsCertificate(mpnsCertificate);
		this.setCertificateKey(certificateKey);
	}
	
	public String getMpnsCertificate() {
		return mpnsCertificate;
	}

	public void setMpnsCertificate(String mpnsCertificate) {
		this.mpnsCertificate = mpnsCertificate;
	}

	public String getCertificateKey() {
		return certificateKey;
	}

	public void setCertificateKey(String certificateKey) {
		this.certificateKey = certificateKey;
	}	
		
	@Override
	public List<SimpleEntry<String, String>> getProperties() {
		ArrayList<SimpleEntry<String, String>> result = new ArrayList<SimpleEntry<String, String>>();
		result.add(new SimpleEntry<String, String>("MpnsCertificate",getMpnsCertificate()));
		result.add(new SimpleEntry<String, String>("CertificateKey",getCertificateKey()));
		return result;
	}

	@Override
	public String getRootTagName() {
		return "MpnsCredential";
	}
}
