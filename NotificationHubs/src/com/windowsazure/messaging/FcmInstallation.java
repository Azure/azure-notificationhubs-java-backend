package com.windowsazure.messaging;

/**
 * This class represents a Firebase Cloud Messaging installation.
 */
public class FcmInstallation extends Installation {

    /**
     * Creates a new instance of the FcmInstallation class.
     *
     * @param installationId The ID for the installation.
     */
    public FcmInstallation(String installationId) {
        super(installationId, NotificationPlatform.Gcm, null, (String[]) null);
    }

    /**
     * Creates a new instance of the FcmInstallation class.
     *
     * @param installationId The ID for the installation.
     * @param tags           The tags for the installation.
     */
    public FcmInstallation(String installationId, String... tags) {
        super(installationId, NotificationPlatform.Gcm, null, tags);
    }

    /**
     * Creates a new instance of the BaiduInstallation class.
     *
     * @param installationId The ID for the installation.
     */
    public FcmInstallation(String installationId, String pushChannel) {
        super(installationId, NotificationPlatform.Gcm, pushChannel, (String[]) null);
    }

    /**
     * Creates a new instance of the BaiduInstallation class.
     * @param installationId The ID for the installation.
     * @param pushChannel The device specific push channel for the installation.
     * @param tags The tags for the installation.
     */
    public FcmInstallation(String installationId, String pushChannel, String... tags) {
        super(installationId, NotificationPlatform.Gcm, pushChannel, tags);
    }
}
