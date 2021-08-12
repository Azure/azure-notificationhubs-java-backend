//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This class represents a WNS secondary tile.
 */
public class WnsSecondaryTile {
    private String pushChannel;
    private boolean pushChannelExpired;
    private List<String> tags;
    private Map<String, InstallationTemplate> templates;

    /**
     * Creates a new instance of the WnsSecondaryTile class.
     */
    public WnsSecondaryTile() {

    }

    /**
     * Creates a new instance of the WnsSecondaryTile class.
     * @param pushChannel The push channel URI for the WNS secondary tile.
     */
    public WnsSecondaryTile(String pushChannel) {
        this(pushChannel, (String[]) null);
    }

    /**
     * Creates a new instance of the WnsSecondaryTile class.
     * @param pushChannel The push channel URI for the WNS secondary tile.
     * @param tags The tags for the WNS secondary tile.
     */
    public WnsSecondaryTile(String pushChannel, String... tags) {
        this.pushChannel = pushChannel;

        if (tags != null) {
            for (String tag : tags) {
                this.addTag(tag);
            }
        }
    }

    /**
     * Gets the push channel for the WNS secondary tile.
     * @return The push channel for the WNS secondary tile.
     */
    public String getPushChannel() { return pushChannel; }

    /**
     * Sets the push channel for the WNS secondary tile.
     * @param value The push channel for the WNS secondary tile to set.
     */
    public void setPushChannel(String value) { pushChannel = value; }

    /**
     * Gets whether the push channel is expired.
     * @return true if the push channel has expired, otherwise false.
     */
    public boolean isPushChannelExpired() { return pushChannelExpired; }

    /**
     * Gets the tags for the WNS secondary tile.
     * @return The tags for the WNS secondary tile.
     */
    public List<String> getTags() { return tags; }

    /**
     * Adds a tag to the WNS secondary tile.
     * @param tag The tag to add to the WNS secondary tile.
     */
    public void addTag(String tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }

        tags.add(tag);
    }

    /**
     * Removes a tag from the WNS secondary tile.
     * @param tag The tag to remove from the WNS secondary tile.
     */
    public void removeTag(String tag) {
        if (tags == null) {
            return;
        }

        tags.remove(tag);
    }

    /**
     * Clears the tags from the WNS secondary tile.
     */
    public void clearTags() {
        if (tags == null) {
            return;
        }

        tags.clear();
    }

    /**
     * Gets the WNS secondary tile templates.
     * @return The WNS secondary tile templates.
     */
    public Map<String, InstallationTemplate> getTemplates() {
        return templates;
    }

    /**
     * Adds a template to the WNS secondary tile.
     * @param templateName The name of the template.
     * @param template The installation template to add with the given name.
     */
    public void addTemplate(String templateName, InstallationTemplate template) {
        if (templates == null) {
            templates = new HashMap<>();
        }

        templates.put(templateName, template);
    }

    /**
     * Removes a template from the WNS secondary tile.
     * @param templateName The name of the template to remove from the WNS secondary tile.
     */
    public void removeTemplate(String templateName) {
        if (templates == null) {
            return;
        }

        templates.remove(templateName);
    }

    /**
     * Clears the templates from the WNS secondary tile.
     */
    public void clearTemplates() {
        if (templates == null) {
            return;
        }

        templates.clear();
    }

    public static WnsSecondaryTile fromJson(String json) {
        return new Gson().fromJson(json, WnsSecondaryTile.class);
    }

    public static WnsSecondaryTile fromJson(InputStream json) throws IOException {
        return WnsSecondaryTile.fromJson(IOUtils.toString(json, StandardCharsets.UTF_8));
    }

    public String toJson() {
        return new GsonBuilder().disableHtmlEscaping().create().toJson(this);
    }
}
