package com.windowsazure.messaging;

/**
 * This class represents an Amazon Device
 */
public class AdmInstallation extends Installation {

    /**
     * Creates a new instance of the AdmInstallation class.
     *
     * @param installationId The ID for the installation.
     */
    public AdmInstallation(String installationId) {
        super(installationId, NotificationPlatform.Adm, null, null);
    }

    /**
     * Creates a new instance of the AdmInstallation class.
     *
     * @param installationId The ID for the installation.
     * @param tags           The tags for the installation.
     */
    public AdmInstallation(String installationId, String... tags) {
        super(installationId, NotificationPlatform.Adm, null, tags);
    }

    /**
     * Creates a new instance of the AdmInstallation class.
     *
     * @param installationId The ID for the installation.
     */
    public AdmInstallation(String installationId, String pushChannel) {
        super(installationId, NotificationPlatform.Adm, pushChannel, (String[]) null);
    }

    /**
     * Creates a new instance of the AdmInstallation class.
     * @param installationId The ID for the installation.
     * @param pushChannel The device specific push channel for the installation.
     * @param tags The tags for the installation.
     */
    public AdmInstallation(String installationId, String pushChannel, String... tags) {
        super(installationId, NotificationPlatform.Adm, pushChannel, tags);
    }
}
