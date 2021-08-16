package com.windowsazure.messaging;

/**
 * This class represents an installation for browser push.
 */
public class BrowserInstallation extends BaseInstallation {
    private BrowserPushChannel pushChannel;

    /**
     * Creates a new instance of the BrowserInstallation class.
     *
     * @param installationId The ID for the installation.
     */
    public BrowserInstallation(String installationId) {
        this(installationId, (String[]) null);
    }

    /**
     * Creates a new instance of the BrowserInstallation class.
     *
     * @param installationId The ID for the installation.
     * @param tags           The tags for the installation.
     */
    public BrowserInstallation(String installationId, String... tags) {
        this(installationId, null, tags);
    }

    /**
     * Creates a new instance of the Installation class.
     *
     * @param installationId The ID for the installation.
     */
    public BrowserInstallation(String installationId, BrowserPushChannel pushChannel) {
        this(installationId, pushChannel, (String[]) null);
    }

    /**
     * Creates a new instance of the Installation class.
     * @param installationId The ID for the installation.
     * @param pushChannel The device specific push channel for the installation.
     * @param tags The tags for the installation.
     */
    public BrowserInstallation(String installationId, BrowserPushChannel pushChannel, String... tags) {
        super(installationId, NotificationPlatform.Browser, tags);
        this.pushChannel = pushChannel;
    }

    /**
     * Gets the PNS specific push channel for the installation.
     *
     * @return The PNS specific push channel for the installation.
     */
    public BrowserPushChannel getPushChannel() { return pushChannel; }

    /**
     * Sets the PNS specific push channel for the installation.
     *
     * @param value The PNS specific push channel for the installation
     */
    public void setPushChannel(BrowserPushChannel value) { pushChannel = value; }
}
