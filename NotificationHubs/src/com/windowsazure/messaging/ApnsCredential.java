//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

public final class ApnsCredential extends PnsCredential {	
	public static final String PROD_ENDPOINT="gateway.push.apple.com";
	public static final String SANDBOX_ENDPOINT="gateway.sandbox.push.apple.com";
	public static final String APNS2_PROD_ENDPOINT="https://api.push.apple.com:443/3/device";
	public static final String APNS2_DEV_ENDPOINT="https://api.development.push.apple.com:443/3/device";
	
	private String endpoint;
	private String apnsCertificate;
	private String certificateKey;	
	private String token;
	private String keyId;
	private String appName;
	private String appId;
	
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

	public void setCertificateKey(String certificateKey) {	this.certificateKey = certificateKey; }

	public String getToken() { return token; }

	public void setToken(String token) { this.token = token; }

	public String getKeyId() { return keyId; }

	public void setKeyId(String keyId) { this.keyId = keyId; }

	public String getAppName() { return appName; }

	public void setAppName(String appName) { this.appName = appName; }

	public String getAppId() { return appId; }

	public void setAppId(String appId) { this.appId = appId; }
	
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
