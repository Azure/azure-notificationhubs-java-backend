//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

/**
 * This class represents an Apple installation.
 */
public class AppleInstallation extends Installation {

    /**
     * Creates a new instance of the AppleInstallation class.
     *
     * @param installationId The ID for the installation.
     */
    public AppleInstallation(String installationId) {
        super(installationId, NotificationPlatform.Apns, null, (String[])null);
    }

    /**
     * Creates a new instance of the AppleInstallation class.
     *
     * @param installationId The ID for the installation.
     * @param tags           The tags for the installation.
     */
    public AppleInstallation(String installationId, String... tags) {
        super(installationId, NotificationPlatform.Apns, null, tags);
    }

    /**
     * Creates a new instance of the AppleInstallation class.
     *
     * @param installationId The ID for the installation.
     * @param pushChannel The push channel for the installation.
     */
    public AppleInstallation(String installationId, String pushChannel) {
        super(installationId, NotificationPlatform.Apns, pushChannel, (String[]) null);
    }

    /**
     * Creates a new instance of the AppleInstallation class.
     * @param installationId The ID for the installation.
     * @param pushChannel The device specific push channel for the installation.
     * @param tags The tags for the installation.
     */
    public AppleInstallation(String installationId, String pushChannel, String... tags) {
        super(installationId, NotificationPlatform.Apns, pushChannel, tags);
    }
}
