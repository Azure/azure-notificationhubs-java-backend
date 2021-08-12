package com.windowsazure.messaging;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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
        this(installationId, null, null, tags);
    }

    /**
     * Creates a new instance of the Installation class.
     *
     * @param installationId The ID for the installation.
     * @param platform       The platform for the installation.
     */
    public BrowserInstallation(String installationId, NotificationPlatform platform, BrowserPushChannel pushChannel) {
        this(installationId, platform, pushChannel, (String[]) null);
    }

    /**
     * Creates a new instance of the Installation class.
     * @param installationId The ID for the installation.
     * @param platform The platform for the installation
     * @param pushChannel The device specific push channel for the installation.
     * @param tags The tags for the installation.
     */
    public BrowserInstallation(String installationId, NotificationPlatform platform, BrowserPushChannel pushChannel, String... tags) {
        super(installationId, platform, tags);
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

    /**
     * Creates an installation from the JSON string.
     *
     * @param json The JSON string that represents the installation.
     * @return An installation created from the JSON string.
     */
    public static BrowserInstallation fromJson(String json) {
        return new Gson().fromJson(json, BrowserInstallation.class);
    }

    /**
     * Creates an installation from the JSON stream.
     *
     * @param json The JSON string that represents the installation.
     * @return An installation created from the JSON stream.
     * @throws IOException An exception reading from the stream occurred.
     */
    public static BrowserInstallation fromJson(InputStream json) throws IOException {
        return BrowserInstallation.fromJson(IOUtils.toString(json, StandardCharsets.UTF_8));
    }
}
