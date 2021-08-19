//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

/**
 * This class represents a Windows Phone PNS installation
 */
public class MpnsInstallation extends Installation {

    /**
     * Creates a new instance of the MpnsInstallation class.
     *
     * @param installationId The ID for the installation.
     */
    public MpnsInstallation(String installationId) {
        super(installationId, NotificationPlatform.Mpns, null, (String[]) null);
    }

    /**
     * Creates a new instance of the MpnsInstallation class.
     *
     * @param installationId The ID for the installation.
     * @param tags           The tags for the installation.
     */
    public MpnsInstallation(String installationId, String... tags) {
        super(installationId, NotificationPlatform.Mpns, null, tags);
    }

    /**
     * Creates a new instance of the MpnsInstallation class.
     *
     * @param installationId The ID for the installation.
     */
    public MpnsInstallation(String installationId, String pushChannel) {
        super(installationId, NotificationPlatform.Mpns, pushChannel, (String[]) null);
    }

    /**
     * Creates a new instance of the MpnsInstallation class.
     * @param installationId The ID for the installation.
     * @param pushChannel The device specific push channel for the installation.
     * @param tags The tags for the installation.
     */
    public MpnsInstallation(String installationId, String pushChannel, String... tags) {
        super(installationId, NotificationPlatform.Mpns, pushChannel, tags);
    }
}
