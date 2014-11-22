package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

public final class WindowsCredential extends PnsCredential {	
	private String packageSid;
	private String secretKey;
	
	public WindowsCredential(){
		this(null,null);
	}
		
	public WindowsCredential(String packageSid, String secretKey){
		super();
		this.setPackageSid(packageSid);
		this.setSecretKey(secretKey);
	}
	
	public String getPackageSid() {
		return packageSid;
	}

	public void setPackageSid(String packageSid) {
		this.packageSid = packageSid;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}	
	
	@Override
	public List<SimpleEntry<String, String>> getProperties() {
		ArrayList<SimpleEntry<String, String>> result = new ArrayList<SimpleEntry<String, String>>();
		result.add(new SimpleEntry<String, String>("PackageSid",getPackageSid()));
		result.add(new SimpleEntry<String, String>("SecretKey",getSecretKey()));
		return result;
	}

	@Override
	public String getRootTagName() {
		return "WnsCredential";
	}
}
