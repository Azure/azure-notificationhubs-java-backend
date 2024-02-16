//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

/**
 * This class represents a Firebase Cloud Messaging V1 installation.
 */
public class FcmV1Installation extends Installation {

    /**
     * Creates a new instance of the FcmV1Installation class.
     *
     * @param installationId The ID for the installation.
     */
    public FcmV1Installation(String installationId) {
        super(installationId, NotificationPlatform.FcmV1, null, (String[]) null);
    }

    /**
     * Creates a new instance of the FcmV1Installation class.
     *
     * @param installationId The ID for the installation.
     * @param tags           The tags for the installation.
     */
    public FcmV1Installation(String installationId, String... tags) {
        super(installationId, NotificationPlatform.FcmV1, null, tags);
    }

    /**
     * Creates a new instance of the FcmV1Installation class.
     *
     * @param installationId The ID for the installation.
     */
    public FcmV1Installation(String installationId, String pushChannel) {
        super(installationId, NotificationPlatform.FcmV1, pushChannel, (String[]) null);
    }

    /**
     * Creates a new instance of the FcmV1Installation class.
     * @param installationId The ID for the installation.
     * @param pushChannel The device specific push channel for the installation.
     * @param tags The tags for the installation.
     */
    public FcmV1Installation(String installationId, String pushChannel, String... tags) {
        super(installationId, NotificationPlatform.FcmV1, pushChannel, tags);
    }
}
