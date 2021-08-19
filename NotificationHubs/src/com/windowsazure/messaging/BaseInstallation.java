//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class BaseInstallation implements Cloneable {
    private String installationId;
    private String userId;
    private NotificationPlatform platform;
    private boolean pushChannelExpired;
    private String expirationTime;
    private List<String> tags;
    private Map<String, InstallationTemplate> templates;

    /**
     * Creates an installation with an installation ID, platform and push channel.
     *
     * @param installationId The ID for the installation.
     * @param platform       The platform for the installation.
     */
    public BaseInstallation(String installationId, NotificationPlatform platform) {
        this(installationId, platform, (String[]) null);
    }

    /**
     * Creates a new instance of the BaseInstallation class.
     * @param installationId The ID for the installation.
     * @param platform The platform for the installation.
     * @param tags The tags for the installation.
     */
    public BaseInstallation(String installationId, NotificationPlatform platform, String... tags) {
        // Validate that this is not FCM
        validateNotificationPlatform(platform);

        this.installationId = installationId;
        this.platform = platform;
        if (tags != null) {
            for (String tag : tags) {
                this.addTag(tag);
            }
        }
    }

    /**
     * Clones the current registration.
     * @return A clone of the current registration.
     */
    public BaseInstallation clone() {
        try {
            return (BaseInstallation) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Validates that the platform is not FCM. Currently, Notification Hubs supports
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
     * @param value The ID for the installation.
     */
    public void setInstallationId(String value) {
        installationId = value;
    }

    /**
     * Gets whether the push channel has expired
     *
     * @return Returns true if expired, false otherwise.
     */
    public boolean isPushChannelExpired() {
        return pushChannelExpired;
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
     * @param value The expiration time for the installation.
     */
    public void setExpirationTime(Date value) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSS'Z'");
        expirationTime = formatter.format(value);
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
     * @param value The platform for the installation.
     */
    public void setPlatform(NotificationPlatform value) {
        // Validate that this is not FCM
        validateNotificationPlatform(value);

        platform = value;
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
     * @param value The user ID for the installation.
     */
    public void setUserId(String value) {
        userId = value;
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
        if (tags == null) {
            tags = new ArrayList<>();
        }

        tags.add(tag);
    }

    /**
     * Removes a tag from the installation.
     *
     * @param tag The tag to remove from the installation.
     */
    public void removeTag(String tag) {
        if (tags == null) {
            return;
        }

        tags.remove(tag);
    }

    /**
     * Clears the tags for the installation.
     */
    public void clearTags() {
        if (tags == null) {
            return;
        }

        tags.clear();
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
        if (templates == null) {
            templates = new HashMap<>();
        }

        templates.put(templateName, template);
    }

    /**
     * Removes an installation template based upon the template name.
     *
     * @param templateName The name of the installation template to remove.
     */
    public void removeTemplate(String templateName) {
        if (templates == null) {
            return;
        }

        templates.remove(templateName);
    }

    /**
     * Clears the installation templates.
     */
    public void clearTemplates() {
        if (templates == null) {
            return;
        }

        templates.clear();
    }

    /**
     * Converts the installation to a JSON string.
     *
     * @return The JSON string representation of the installation.
     */
    public String toJson() {
        return new GsonBuilder().disableHtmlEscaping().create().toJson(this);
    }

    /**
     * Creates an installation from the JSON string.
     *
     * @param jsonString The JSON string that represents the installation.
     * @return An installation created from the JSON string.
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseInstallation> T fromJson(String jsonString) {

        Gson gson = new GsonBuilder()
            .registerTypeAdapter(BaseInstallation.class, new BaseInstallationDeserializer())
            .create();

        return (T)gson.fromJson(jsonString, BaseInstallation.class);
    }

    /**
     * Creates an installation from the JSON stream.
     *
     * @param json The JSON string that represents the installation.
     * @return An installation created from the JSON stream.
     * @throws IOException An exception reading from the stream occurred.
     */
    public static <T extends BaseInstallation> T fromJson(InputStream json) throws IOException {
        return fromJson(IOUtils.toString(json, StandardCharsets.UTF_8));
    }
}
