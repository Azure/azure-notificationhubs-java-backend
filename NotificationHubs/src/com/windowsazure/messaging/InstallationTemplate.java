package com.windowsazure.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class InstallationTemplate {
	private String body;
	private Map<String, String> headers;
	private String expiry;
	private List<String> tags;
	
	public InstallationTemplate(){
		this(null);
	}
	
	public InstallationTemplate(String body){
		this(body, null);
	}
		
	public InstallationTemplate(String body, String tag){
		this.body = body;
		if(tag != null){			
			this.addTag(tag);			
		}
	}	
	
	public String getBody() {
		return body;
	}	
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public void addHeader(String headerName, String headerValue){
		if(this.headers == null){
			this.headers = new HashMap<String,String>();
		}
		
		this.headers.put(headerName, headerValue);
	}
	
	public String getExpiry() {
		return expiry;
	}
	
	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}
	
	public List<String> getTags() {
		return tags;
	}
	
	public void addTag(String tag) {
		if(this.tags == null){
			this.tags = new ArrayList<String>();
		}
		
		this.tags.add(tag);
	}
	
	public static InstallationTemplate fromJson(String json){
		return new Gson().fromJson(json, InstallationTemplate.class);
	}
	
	public static Installation fromJson(InputStream json) throws IOException{
		return Installation.fromJson(IOUtils.toString(json));
	}
	
	public String toJson(){
		return new GsonBuilder().disableHtmlEscaping().create().toJson(this);
	}
}
