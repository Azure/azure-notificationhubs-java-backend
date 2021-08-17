package com.windowsazure.messaging;

/**
 * This class represents a Baidu Installation.
 */
public class BaiduInstallation extends Installation {

    /**
     * Creates a new instance of the BaiduInstallation class.
     *
     * @param installationId The ID for the installation.
     */
    public BaiduInstallation(String installationId) {
        super(installationId, NotificationPlatform.Baidu, null, (String[])null);
    }

    /**
     * Creates a new instance of the BaiduInstallation class.
     *
     * @param installationId The ID for the installation.
     * @param tags           The tags for the installation.
     */
    public BaiduInstallation(String installationId, String... tags) {
        super(installationId, NotificationPlatform.Baidu, null, tags);
    }

    /**
     * Creates a new instance of the BaiduInstallation class.
     *
     * @param installationId The ID for the installation.
     * @param pushChannel The push channel for the Baidu installation.
     */
    public BaiduInstallation(String installationId, String pushChannel) {
        super(installationId, NotificationPlatform.Baidu, pushChannel, (String[]) null);
    }

    /**
     * Creates a new instance of the BaiduInstallation class.
     * @param installationId The ID for the installation.
     * @param pushChannel The device specific push channel for the installation.
     * @param tags The tags for the installation.
     */
    public BaiduInstallation(String installationId, String pushChannel, String... tags) {
        super(installationId, NotificationPlatform.Baidu, pushChannel, tags);
    }
}
