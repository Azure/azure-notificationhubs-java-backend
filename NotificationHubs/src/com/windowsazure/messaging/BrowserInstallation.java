//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

/**
 * This class represents a Browser installation.
 */
public class BrowserInstallation extends Installation {

    /**
     * Creates a new instance of the BrowserInstallation class.
     *
     * @param installationId The ID for the installation.
     */
    public BrowserInstallation(String installationId) {
        super(installationId, NotificationPlatform.Browser, null, (String[]) null);
    }

    /**
     * Creates a new instance of the BrowserInstallation class.
     *
     * @param installationId The ID for the installation.
     * @param tags           The tags for the installation.
     */
    public BrowserInstallation(String installationId, String... tags) {
        super(installationId, NotificationPlatform.Browser, null, tags);
    }

    /**
     * Creates a new instance of the BrowserInstallation class.
     *
     * @param installationId The ID for the installation.
     * @param browserPushSubscription The browser push subscription.
     */
    public BrowserInstallation(String installationId, BrowserPushSubscription browserPushSubscription) {
        super(installationId, NotificationPlatform.Browser, browserPushSubscription.toJson(), (String[]) null);
    }

    /**
     * Creates a new instance of the BrowserInstallation class.
     * @param installationId The ID for the installation.
     * @param browserPushSubscription The browser push subscription.
     * @param tags The tags for the installation.
     */
    public BrowserInstallation(String installationId, BrowserPushSubscription browserPushSubscription, String... tags) {
        super(installationId, NotificationPlatform.Browser, browserPushSubscription.toJson(), tags);
    }
}
