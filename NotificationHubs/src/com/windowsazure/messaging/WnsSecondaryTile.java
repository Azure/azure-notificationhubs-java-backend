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

public class WnsSecondaryTile {
	private String pushChannel;
	private boolean pushChannelExpired;
	private List<String> tags;
	private Map<String, InstallationTemplate> templates;
	
	public WnsSecondaryTile(){
		this(null);
	}
	
	public WnsSecondaryTile(String pushChannel){
		this(pushChannel, (String[])null);
	}
	
	public WnsSecondaryTile(String pushChannel, String... tags){
		this.pushChannel = pushChannel;
		
		if(tags != null){	
			for(String tag : tags){
				this.addTag(tag);
			}
		}
	}
	
	public String getPushChannel() {
		return pushChannel;
	}
	
	public void setPushChannel(String pushChannel) {
		this.pushChannel = pushChannel;
	}
	
	public boolean isPushChannelExpired() {
		return pushChannelExpired;
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
	
	public Map<String, InstallationTemplate> getTemplates() {
		return templates;
	}
	
	public void addTemplate(String templateName, InstallationTemplate template) {
		if(this.templates == null){
			this.templates = new HashMap<String, InstallationTemplate>();
		}
		
		this.templates.put(templateName, template);
	}
	
	public static WnsSecondaryTile fromJson(String json){
		return new Gson().fromJson(json, WnsSecondaryTile.class);
	}
	
	public static WnsSecondaryTile fromJson(InputStream json) throws IOException{
		return WnsSecondaryTile.fromJson(IOUtils.toString(json));
	}
	
	public String toJson(){
		return new GsonBuilder().disableHtmlEscaping().create().toJson(this);
	}
}
