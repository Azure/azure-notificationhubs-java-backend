//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

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
}
