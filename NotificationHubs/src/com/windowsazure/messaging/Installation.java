//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Represents an installation for Azure Notification Hubs
 */
public class Installation extends BaseInstallation {

    private String pushChannel;

    /**
     * Creates a new instance of the Installation class.
     *
     * @param installationId The ID for the installation.
     */
    public Installation(String installationId) {
        this(installationId, (String[]) null);
    }

    /**
     * Creates a new instance of the Installation class.
     *
     * @param installationId The ID for the installation.
     * @param tags           The tags for the installation.
     */
    public Installation(String installationId, String... tags) {
        this(installationId, null, null, tags);
    }

    /**
     * Creates a new instance of the Installation class.
     *
     * @param installationId The ID for the installation.
     * @param platform       The platform for the installation.
     */
    public Installation(String installationId, NotificationPlatform platform, String pushChannel) {
        this(installationId, platform, pushChannel, (String[]) null);
    }

    /**
     * Creates a new instance of the Installation class.
     * @param installationId The ID for the installation.
     * @param platform The platform for the installation
     * @param pushChannel The device specific push channel for the installation.
     * @param tags The tags for the installation.
     */
    public Installation(String installationId, NotificationPlatform platform, String pushChannel, String... tags) {
        super(installationId, platform, tags);
        this.pushChannel = pushChannel;
    }

    /**
     * Gets the PNS specific push channel for the installation.
     *
     * @return The PNS specific push channel for the installation.
     */
    public String getPushChannel() { return pushChannel; }

    /**
     * Sets the PNS specific push channel for the installation.
     *
     * @param value The PNS specific push channel for the installation
     */
    public void setPushChannel(String value) { pushChannel = value; }

    /**
     * Creates an installation from the JSON string.
     *
     * @param json The JSON string that represents the installation.
     * @return An installation created from the JSON string.
     */
    public static Installation fromJson(String json) {
        return new Gson().fromJson(json, Installation.class);
    }

    /**
     * Creates an installation from the JSON stream.
     *
     * @param json The JSON string that represents the installation.
     * @return An installation created from the JSON stream.
     * @throws IOException An exception reading from the stream occurred.
     */
    public static Installation fromJson(InputStream json) throws IOException {
        return Installation.fromJson(IOUtils.toString(json, StandardCharsets.UTF_8));
    }
}
