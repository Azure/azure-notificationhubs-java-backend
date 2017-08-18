package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

public final class ApnsCredential extends PnsCredential {	
	public static final String PROD_ENDPOINT="gateway.push.apple.com";
	public static final String SANDBOX_ENDPOINT="gateway.sandbox.push.apple.com";
	
	private String endpoint;
	private String apnsCertificate;
	private String certificateKey;
	
	public ApnsCredential(){
		this(null,null);
	}
	
	public ApnsCredential(String endpoint){
		this(null,null, endpoint);
	}
	
	public ApnsCredential(String apnsCertificate, String certificateKey){
		this(apnsCertificate, certificateKey, PROD_ENDPOINT);
	}
	
	public ApnsCredential(String apnsCertificate, String certificateKey, String endpoint){
		super();
		this.setApnsCertificate(apnsCertificate);
		this.setCertificateKey(certificateKey);
		this.setEndpoint(endpoint);
	}
	
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getApnsCertificate() {
		return apnsCertificate;
	}

	public void setApnsCertificate(String apnsCertificate) {
		this.apnsCertificate = apnsCertificate;
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
		result.add(new SimpleEntry<String, String>("Endpoint",getEndpoint()));
		result.add(new SimpleEntry<String, String>("ApnsCertificate",getApnsCertificate()));
		result.add(new SimpleEntry<String, String>("CertificateKey",getCertificateKey()));
		return result;
	}

	@Override
	public String getRootTagName() {
		return "ApnsCredential";
	}
}
