//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

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

/**
 * This class represents an installation's WNS secondary tiles.
 */
public class WnsSecondaryTile {
    private String pushChannel;
    private boolean pushChannelExpired;
    private List<String> tags;
    private Map<String, InstallationTemplate> templates;

    /**
     * Creates a new WNS secondary tile.
     */
    public WnsSecondaryTile() {
        this(null);
    }

    /**
     * Creates a WINS secondary tile with a push channel URI.
     *
     * @param pushChannel The push channel URI.
     */
    public WnsSecondaryTile(String pushChannel) {
        this(pushChannel, (String[]) null);
    }

    /**
     * Creates a WNS secondary tile with push channel URI and tags.
     *
     * @param pushChannel The push channel URI.
     * @param tags        The tags for the WNS secondary tile.
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
     * Gets the push channel URI for the WNS secondary tile.
     *
     * @return The push channel URI for the WNS secondary tile.
     */
    public String getPushChannel() {
        return pushChannel;
    }

    /**
     * Sets the push channel URI for the WNS secondary tile.
     *
     * @param pushChannel The push channel URI for the WNS secondary tile.
     */
    public void setPushChannel(String pushChannel) {
        this.pushChannel = pushChannel;
    }

    /**
     * Gets whether the push channel is expired.
     *
     * @return true if the channel is expired, else false.
     */
    public boolean isPushChannelExpired() {
        return pushChannelExpired;
    }

    /**
     * Gets the tags for the WNS secondary tile.
     *
     * @return The tags for the WNS secondary tile.
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Adds a tag to the WNS secondary tile tags;
     *
     * @param tag The tag to be added.
     */
    public void addTag(String tag) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }

        this.tags.add(tag);
    }

    /**
     * Removes a tag from the WNS secondary tile tags;
     *
     * @param tag The tag to be removed.
     */
    public void removeTag(String tag) {
        if (this.tags == null) {
            return;
        }

        this.tags.remove(tag);
    }

    /**
     * Clears the tags from the WNS secondary tile.
     */
    public void clearTags() {
        if (this.tags == null) {
            return;
        }

        this.tags.clear();
    }

    /**
     * Gets the installation templates for the WNS secondary tile.
     *
     * @return The installation templates for the WNS secondary tile.
     */
    public Map<String, InstallationTemplate> getTemplates() {
        return templates;
    }

    /**
     * Adds a template to the WNS secondary tiles by name.
     *
     * @param templateName The template name.
     * @param template     The installation template.
     */
    public void addTemplate(String templateName, InstallationTemplate template) {
        if (this.templates == null) {
            this.templates = new HashMap<>();
        }

        this.templates.put(templateName, template);
    }

    /**
     * Removes a template by name from the WNS secondary tile templates.
     *
     * @param templateName The name of the template to remove.
     */
    public void removeTemplate(String templateName) {
        if (this.templates == null) {
            return;
        }

        this.templates.remove(templateName);
    }

    /**
     * Clears the WNS secondary tile templates.
     */
    public void clearTemplates() {
        if (this.templates == null) {
            return;
        }

        this.templates.clear();
    }

    /**
     * Creates a WNS secondary tile from a JSON string.
     *
     * @param json The JSON string to parse into a WNS secondary tile.
     * @return The WNS secondary tile created from the JSON string.
     */
    public static WnsSecondaryTile fromJson(String json) {
        return new Gson().fromJson(json, WnsSecondaryTile.class);
    }

    /**
     * Creates a WNS secondary tile from a JSON stream.
     *
     * @param json The JSON stream to parse into a WNS secondary tile.
     * @return The WNS secondary tile created from the JSON string.
     * @throws IOException If there was an exception reading from the stream.
     */
    public static WnsSecondaryTile fromJson(InputStream json) throws IOException {
        return WnsSecondaryTile.fromJson(IOUtils.toString(json));
    }

    /**
     * Creates a JSON string representation of the WNS secondary tile.
     *
     * @return A JSON string representation of the WNS secondary tile.
     */
    public String toJson() {
        return new GsonBuilder().disableHtmlEscaping().create().toJson(this);
    }
}
