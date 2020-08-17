//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Installation {
	        private String installationId;
	        private String userId;
	        private NotificationPlatform platform;
	        private String pushChannel;
	        private boolean pushChannelExpired;
	        private String expirationTime;
	        private List<String> tags;
	        private Map<String, InstallationTemplate> templates;
	        private Map<String, WnsSecondaryTile> secondaryTiles;

	        public Installation(){
	        	this(null);
	        }

	        public Installation(String installationId){
	        	this(installationId, (String[])null);
	        }

	        public Installation(String installationId, String... tags){
	        	this(installationId, null, null, null, (String[])null);
	        }

	        public Installation(String installationId, NotificationPlatform platform, String pushChannel){
	        	this(installationId, platform, pushChannel, null, (String[])null);
	        }

	        public Installation(String installationId, NotificationPlatform platform, String pushChannel, String userId, String... tags){
	        	this.installationId = installationId;
	        	this.userId = userId;
	        	this.platform = platform;
	        	this.pushChannel = pushChannel;
	        	if(tags != null){
	    			for(String tag : tags){
	    				this.addTag(tag);
	    			}
	    		}
	        }

			public String getInstallationId() {
				return installationId;
			}

			public void setInstallationId(String installationId) {
				this.installationId = installationId;
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

			public Date getExpirationTime() {
				return javax.xml.bind.DatatypeConverter.parseDateTime(expirationTime).getTime();
			}

			public void setExpirationTime(Date expirationTime) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSS'Z'");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                this.expirationTime = formatter.format(expirationTime);
            }

			public NotificationPlatform getPlatform() {
				return platform;
			}

			public void setPlatform(NotificationPlatform platform) {
				this.platform = platform;
			}

			public String getUserId() {
				return userId;
			}

			public void setUserId(String userId) {
				this.userId = userId;
			}

			public List<String> getTags() {
				return tags;
			}

			public void addTag(String tag) {
				if(this.tags == null){
					this.tags = new ArrayList<>();
				}

				this.tags.add(tag);
			}

			public Map<String, InstallationTemplate> getTemplates() {
				return templates;
			}

			public void addTemplate(String templateName, InstallationTemplate template) {
				if(this.templates == null){
					this.templates = new HashMap<>();
				}

				this.templates.put(templateName, template);
			}

			public Map<String, WnsSecondaryTile> getSecondaryTiles() {
				return secondaryTiles;
			}

			public void addSecondaryTile(String tileName, WnsSecondaryTile tile) {
				if(this.templates == null){
					this.secondaryTiles = new HashMap<>();
				}

				this.secondaryTiles.put(tileName, tile);
			}

			public static Installation fromJson(String json){
	            return new Gson().fromJson(json, Installation.class);
			}

			public static Installation fromJson(InputStream json) throws IOException{
				return Installation.fromJson(IOUtils.toString(json));
			}

			public String toJson(){
				return new GsonBuilder().disableHtmlEscaping().create().toJson(this);
			}
}
