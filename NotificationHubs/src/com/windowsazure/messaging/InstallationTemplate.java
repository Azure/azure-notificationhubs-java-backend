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
 * Represents an installation template.
 */
public class InstallationTemplate {
    private String body;
    private Map<String, String> headers;
    private String expiry;
    private List<String> tags;

    /**
     * Initializes a new installation template.
     */
    public InstallationTemplate() {
        this(null);
    }

    /**
     * Initializes a new installation template with a body.
     *
     * @param body The body for the installation template.
     */
    public InstallationTemplate(String body) {
        this(body, null);
    }

    /**
     * Initializes a new installation template with a body and tag.
     *
     * @param body The installation template body
     * @param tags The installation template tags .
     */
    public InstallationTemplate(String body, List<String> tags) {
        this.body = body;
        if (tags != null) {
            for (String tag : tags) {
                this.addTag(tag);
            }
        }
    }

    /**
     * Gets the installation template body.
     *
     * @return The installation template body.
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the installation body.
     *
     * @param body The installation body.
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Gets the headers for the installation template.
     *
     * @return The installation template headers.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Adds a header name and value to the headers.
     *
     * @param headerName  The name of the header.
     * @param headerValue The value of the header.
     */
    public void addHeader(String headerName, String headerValue) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }

        this.headers.put(headerName, headerValue);
    }

    /**
     * Removes a header from the collection by name.
     *
     * @param headerName The name of the header to remove.
     */
    public void removeHeader(String headerName) {
        if (this.headers == null) {
            return;
        }

        this.headers.remove(headerName);
    }

    public void clearHeaders() {
        if (this.headers == null) {
            return;
        }

        this.headers.clear();
    }

    /**
     * Gets the installation template expiration.
     *
     * @return The installation template expiration.
     */
    public String getExpiry() {
        return expiry;
    }

    /**
     * Sets the installation template expiration.
     *
     * @param expiry The installation template expiration.
     */
    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    /**
     * Gets the tags for the installation template.
     *
     * @return The installation template tags.
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Adds a tag to the installation template.
     *
     * @param tag The tag to add to the template.
     */
    public void addTag(String tag) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }

        this.tags.add(tag);
    }

    /**
     * Removes a tag from the installation template.
     *
     * @param tag The tag to remove from the installation template.
     */
    public void removeTag(String tag) {
        if (this.tags == null) {
            return;
        }

        this.tags.remove(tag);
    }

    /**
     * Clears the tags for the installation template.
     */
    public void clearTags() {
        if (this.tags == null) {
            return;
        }

        this.tags.clear();
    }

    /**
     * Creates an installation template from the JSON string.
     *
     * @param json The JSON string to create the Installation Template.
     * @return An installation template created from JSON.
     */
    public static InstallationTemplate fromJson(String json) {
        return new Gson().fromJson(json, InstallationTemplate.class);
    }

    /**
     * Creates an installation template from the JSON input stream.
     *
     * @param json The JSON input stream.
     * @return An installation template created from the JSON stream.
     * @throws IOException If there was an issue with the Stream.
     */
    public static Installation fromJson(InputStream json) throws IOException {
        return Installation.fromJson(IOUtils.toString(json));
    }

    /**
     * Converts the installation template to JSON.
     *
     * @return The JSON representation of the installation template.
     */
    public String toJson() {
        return new GsonBuilder().disableHtmlEscaping().create().toJson(this);
    }
}
