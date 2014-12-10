package com.windowsazure.messaging;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

public final class BaiduCredential extends PnsCredential {	
	private String baiduApiKey;
	private String baiduSecretKey;
	private String baiduEndPoint;
	
	public BaiduCredential(){
		this(null, null);
	}
		
	public BaiduCredential(String baiduApiKey,String baiduSecretKey){
		this.setBaiduApiKey(baiduApiKey);
		this.setBaiduSecretKey(baiduSecretKey);
	}
	
	public String getBaiduApiKey() {
		return this.baiduApiKey;
	}

	public void setBaiduApiKey(String baiduApiKey) {
		this.baiduApiKey = baiduApiKey;
	}
	

	public String getBaiduSecretKey() {
		return baiduSecretKey;
	}

	public void setBaiduSecretKey(String baiduSecretKey) {
		this.baiduSecretKey = baiduSecretKey;
	}
	
	public String getBaiduEndPoint() {
		return baiduEndPoint;
	}

	public void setBaiduEndPoint(String baiduEndPoint) {
		this.baiduEndPoint = baiduEndPoint;
	}
		
	@Override
	public List<SimpleEntry<String, String>> getProperties() {
		ArrayList<SimpleEntry<String, String>> result = new ArrayList<SimpleEntry<String, String>>();
		result.add(new SimpleEntry<String, String>("BaiduApiKey",getBaiduApiKey()));
		result.add(new SimpleEntry<String, String>("BaiduSecretKey",getBaiduSecretKey()));
		return result;
	}

	@Override
	public String getRootTagName() {
		return "BaiduCredential";
	}	
}
