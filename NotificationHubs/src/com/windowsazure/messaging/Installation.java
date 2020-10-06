//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Represents an installation for Azure Notification Hubs
 */
public class Installation {

    private String installationId;
    private String userId;
    private NotificationPlatform platform;
    private String pushChannel;
    private String expirationTime;
    private List<String> tags;
    private Map<String, InstallationTemplate> templates;
    private Map<String, WnsSecondaryTile> secondaryTiles;

    /**
     * Creates a new installation.
     */
    public Installation() {
        this(null);
    }

    /**
     * Creates a new installation with the given installation ID.
     *
     * @param installationId The ID for the installation.
     */
    public Installation(String installationId) {
        this(installationId, (String[]) null);
    }

    /**
     * Creates an installation from the Installation ID and tags.
     *
     * @param installationId The ID for the installation.
     * @param tags           The tags for the installation.
     */
    public Installation(String installationId, String... tags) {
        this(installationId, null, null, tags);
    }

    /**
     * Creates an installation with an installation ID, platform and push channel.
     *
     * @param installationId The ID for the installation.
     * @param platform       The platform for the installation.
     * @param pushChannel    The PNS specific channel for device.
     */
    public Installation(String installationId, NotificationPlatform platform, String pushChannel) {
        this(installationId, platform, pushChannel, (String[]) null);
    }

    public Installation(String installationId, NotificationPlatform platform, String pushChannel, String... tags) {
        // Validate that this is not FCM
        validateNotificationPlatform(platform);

        this.installationId = installationId;
        this.platform = platform;
        this.pushChannel = pushChannel;
        if (tags != null) {
            for (String tag : tags) {
                this.addTag(tag);
            }
        }
    }

    /**
     * Validates that the platform is not FCM. Currently Notification Hubs supports
     * FCM Legacy as NotificationPlatform.Gcm Full support for FCM is not currently
     * supported. See https://aka.ms/AA9dpaz
     *
     * @param notificationPlatform The notification platform to verify that it is
     *                             not FCM.
     */
    private static void validateNotificationPlatform(NotificationPlatform notificationPlatform) {
        if (notificationPlatform == NotificationPlatform.Fcm) {
            throw new RuntimeException(
                "FCM is currently not supported, use NotificationPlatform.Gcm which uses FCM Legacy Mode. See https://aka.ms/AA9dpaz");
        }
    }

    /**
     * Gets the ID for the installation.
     *
     * @return The ID for the installation.
     */
    public String getInstallationId() {
        return installationId;
    }

    /**
     * Sets the ID for the installation.
     *
     * @param installationId The ID for the installation.
     */
    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }

    /**
     * Gets the PNS specific push channel for the installation.
     *
     * @return The PNS specific push channel for the installation.
     */
    public String getPushChannel() {
        return pushChannel;
    }

    /**
     * Sets the PNS specific push channel for the installation.
     *
     * @param pushChannel The PNS specific push channel for the installation
     */
    public void setPushChannel(String pushChannel) {
        this.pushChannel = pushChannel;
    }

    /**
     * Gets the expiration time for the installation.
     *
     * @return The installation expiration time.
     */
    public Date getExpirationTime() {
        return javax.xml.bind.DatatypeConverter.parseDateTime(expirationTime).getTime();
    }

    /**
     * Sets the expiration time for the installation.
     *
     * @param expirationTime The expiration time for the installation.
     */
    public void setExpirationTime(Date expirationTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSS'Z'");
        this.expirationTime = formatter.format(expirationTime);
    }

    /**
     * Gets the platform for the installation.
     *
     * @return The platform for the installation.
     */
    public NotificationPlatform getPlatform() {
        return platform;
    }

    /**
     * Sets the platform for the installation. Note that FCM is currently not
     * supported, only GCM which is FCM Legacy. See https://aka.ms/AA9dpaz
     * for more details.
     *
     * @param platform The platform for the installation.
     */
    public void setPlatform(NotificationPlatform platform) {
        // Validate that this is not FCM
        validateNotificationPlatform(platform);

        this.platform = platform;
    }

    /**
     * Gets the user ID for the installation.
     *
     * @return The user ID for the installation.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID for the installation.
     *
     * @param userId The user ID for the installation.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the tags for the installation.
     *
     * @return The tags for the installation.
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Adds a tag to the installation.
     *
     * @param tag The tag to add to the installation;
     */
    public void addTag(String tag) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }

        this.tags.add(tag);
    }

    /**
     * Removes a tag from the installation.
     *
     * @param tag The tag to remove from the installation.
     */
    public void removeTag(String tag) {
        if (this.tags == null) {
            return;
        }

        this.tags.remove(tag);
    }

    /**
     * Clears the tags for the installation.
     */
    public void clearTags() {
        if (this.tags == null) {
            return;
        }

        this.tags.clear();
    }

    /**
     * Gets the installation templates for the installation.
     *
     * @return The installation templates for the installation.
     */
    public Map<String, InstallationTemplate> getTemplates() {
        return templates;
    }

    /**
     * Adds an installation template by name to the installation.
     *
     * @param templateName The name for the installation template.
     * @param template     The template to add to the installation.
     */
    public void addTemplate(String templateName, InstallationTemplate template) {
        if (this.templates == null) {
            this.templates = new HashMap<>();
        }

        this.templates.put(templateName, template);
    }

    /**
     * Removes an installation template based upon the template name.
     *
     * @param templateName The name of the installation template to remove.
     */
    public void removeTemplate(String templateName) {
        if (this.templates == null) {
            return;
        }

        this.templates.remove(templateName);
    }

    /**
     * Clears the installation templates.
     */
    public void clearTemplates() {
        if (this.templates == null) {
            return;
        }

        this.templates.clear();
    }

    /**
     * Gets the secondary tiles for WNS
     *
     * @return The secondary tiles for WNS.
     */
    public Map<String, WnsSecondaryTile> getSecondaryTiles() {
        return secondaryTiles;
    }

    /**
     * Adds a secondary tile to the installation template.
     *
     * @param tileName The name for the tile.
     * @param tile     THe WNS secondary tile.
     */
    public void addSecondaryTile(String tileName, WnsSecondaryTile tile) {
        if (this.templates == null) {
            this.secondaryTiles = new HashMap<>();
        }

        this.secondaryTiles.put(tileName, tile);
    }

    public void removeSecondaryTile(String tileName) {
        if (this.templates == null) {
            return;
        }

        this.secondaryTiles.remove(tileName);
    }

    /**
     * Clears the WNS secondary tiles.
     */
    public void clearSecondaryTiles() {
        if (this.templates == null) {
            return;
        }

        this.secondaryTiles.clear();
    }

    /**
     * Creates an installation from the JSON string.
     *
     * @param json The JSON string that represents the installation.
     * @return An installation created from the JSON string.
     */
    public static Installation fromJson(String json) {
        return new Gson().fromJson(json, Installation.class);
    }

    /**
     * Creates an installation from the JSON stream.
     *
     * @param json The JSON string that represents the installation.
     * @return An installation created from the JSON stream.
     * @throws IOException An exception reading from the stream occurred.
     */
    public static Installation fromJson(InputStream json) throws IOException {
        return Installation.fromJson(IOUtils.toString(json));
    }

    /**
     * Converts the installation to a JSON string.
     *
     * @return The JSON string representation of the installation.
     */
    public String toJson() {
        return new GsonBuilder().disableHtmlEscaping().create().toJson(this);
    }
}
