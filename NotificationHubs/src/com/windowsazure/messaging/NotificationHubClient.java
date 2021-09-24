//----------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
//----------------------------------------------------------------

package com.windowsazure.messaging;

import org.apache.hc.core5.concurrent.FutureCallback;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * This interface represents all actions that can be done on an Azure Notification Hub.
 */
public interface NotificationHubClient {
    /**
     * Creates or updates an installation.
     *
     * @param installation The installation to create or update.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    void createOrUpdateInstallation(BaseInstallation installation) throws NotificationHubsException;

    /**
     * Creates or updates an installation.
     *
     * @param installation The installation to create or update.
     * @param callback     A callback, when invoked, returns nothing.
     */
    void createOrUpdateInstallationAsync(BaseInstallation installation, FutureCallback<Object> callback);

    /**
     * Patches an installation with the given installation ID.
     *
     * @param installationId The installation ID to patch.
     * @param operations     The list of operations to perform on the installation.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    void patchInstallation(String installationId, PartialUpdateOperation... operations)
        throws NotificationHubsException;

    /**
     * Patches an installation with the given installation ID.
     *
     * @param installationId The installation ID to patch.
     * @param callback       A callback, when invoked, returns nothing.
     * @param operations     The list of operations to perform on the installation.
     */
    void patchInstallationAsync(
        String installationId,
        FutureCallback<Object> callback,
        PartialUpdateOperation... operations
    );

    /**
     * Patches an installation with the given installation ID.
     *
     * @param installationId The installation ID to patch.
     * @param operations     The list of operations to perform on the installation.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    void patchInstallation(String installationId, List<PartialUpdateOperation> operations)
        throws NotificationHubsException;

    /**
     * Patches an installation with the given installation ID.
     *
     * @param installationId The installation ID to patch.
     * @param operations     The list of operations to perform on the installation.
     * @param callback       A callback, when invoked, returns nothing.
     */
    void patchInstallationAsync(
        String installationId,
        List<PartialUpdateOperation> operations,
        FutureCallback<Object> callback
    );

    /**
     * Deletes an installation with the given installation ID.
     *
     * @param installationId The installation ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    void deleteInstallation(String installationId) throws NotificationHubsException;

    /**
     * Deletes an installation with the given installation ID.
     *
     * @param installationId The installation ID.
     * @param callback       A callback, when invoked, returns nothing.
     */
    void deleteInstallationAsync(String installationId, FutureCallback<Object> callback);

    /**
     * Gets an installation by the given installation ID.
     *
     * @param installationId The installation ID for the installation to get.
     * @return The matching installation by the installation ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    <T extends BaseInstallation> T getInstallation(String installationId) throws NotificationHubsException;

    /**
     * Gets an installation by the given installation ID.
     *
     * @param installationId The installation ID for the installation to get.
     * @param callback       A callback, when invoked, returns the matching
     *                       installation by the installation ID.
     */
    <T extends BaseInstallation> void getInstallationAsync(String installationId, FutureCallback<T> callback);

    /**
     * Submits a notification hub job such as import or export.
     *
     * @param job The notification hubs job to submit.
     * @return The notification job with status.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    NotificationHubJob submitNotificationHubJob(NotificationHubJob job) throws NotificationHubsException;

    /**
     * Submits a notification hub job such as import or export.
     *
     * @param job      The notification hubs job to submit.
     * @param callback A callback, when invoked, returns the notification job with
     *                 status.
     */
    void submitNotificationHubJobAsync(NotificationHubJob job, FutureCallback<NotificationHubJob> callback);

    /**
     * Gets a notification hub job by the job ID.
     *
     * @param jobId The job ID of the notification hub job to get.
     * @return The notification hub job with the matching job ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    NotificationHubJob getNotificationHubJob(String jobId) throws NotificationHubsException;

    /**
     * Gets a notification hub job by the job ID.
     *
     * @param jobId    The job ID of the notification hub job to get.
     * @param callback A callback, when invoked, returns the notification hub job
     *                 with the matching job ID.
     */
    void getNotificationHubJobAsync(String jobId, FutureCallback<NotificationHubJob> callback);

    /**
     * Gets all notification hub jobs for this namespace.
     *
     * @return All notification hub jobs for this namespace.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    List<NotificationHubJob> getAllNotificationHubJobs() throws NotificationHubsException;

    /**
     * Gets all notification hub jobs for this namespace.
     *
     * @param callback A callback, when invoked, returns all notification hub jobs
     *                 for this namespace.
     */
    void getAllNotificationHubJobsAsync(FutureCallback<List<NotificationHubJob>> callback);

    /**
     * Gets notification telemetry by the notification ID.
     *
     * @param notificationId The notification ID for the telemetry.
     * @return The notification telemetry for the given notification.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    NotificationTelemetry getNotificationTelemetry(String notificationId) throws NotificationHubsException;

    /**
     * Gets notification telemetry by the notification ID.
     *
     * @param notificationId The notification ID for the telemetry.
     * @param callback       A callback, when invoked, returns the notification
     *                       telemetry for the given notification.
     */
    void getNotificationTelemetryAsync(String notificationId, final FutureCallback<NotificationTelemetry> callback);

    /**
     * Create a registrationId, without creating an actual registration. To create
     * use upsert. This method is used when the registration id is stored only on
     * the device.
     *
     * @return The newly created registration ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    String createRegistrationId() throws NotificationHubsException;

    /**
     * Create a registrationId, without creating an actual registration. To create
     * use upsert. This method is used when the registration id is stored only on
     * the device.
     *
     * @param callback A callback with the newly created registration ID.
     */
    void createRegistrationIdAsync(final FutureCallback<String> callback);

    /**
     * This method creates a new registration
     *
     * @param registration A registration object containing the description of the
     *                     registration to create. ETag and registration ID are
     *                     ignored
     * @return The created registration containing the read-only parameters
     * (registration ID, ETag, and expiration time).
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    <T extends Registration> T createRegistration(T registration) throws NotificationHubsException;

    /**
     * This method creates a new registration
     *
     * @param registration A registration object containing the description of the
     *                     registration to create. ETag and registration ID are
     *                     ignored
     * @param callback     A callback when invoked returns created registration
     *                     containing the read-only parameters (registration ID,
     *                     ETag, and expiration time)
     */
    <T extends Registration> void createRegistrationAsync(T registration, final FutureCallback<T> callback);

    /**
     * This method updates an existing registration
     *
     * @param registration A registration object containing the description of the
     *                     registration to update. The registration ID has to be
     *                     populated.
     * @return The updated registration containing the read-only parameters
     * (registration ID, ETag, and expiration time).
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    <T extends Registration> T updateRegistration(T registration) throws NotificationHubsException;

    /**
     * This method updates an existing registration
     *
     * @param registration A registration object containing the description of the
     *                     registration to update. The registration ID must be
     *                     populated.
     * @param callback     A callback when invoked, returns the updated registration
     *                     containing the read-only parameters (registration ID,
     *                     ETag, and expiration time).
     */
    <T extends Registration> void updateRegistrationAsync(T registration, final FutureCallback<T> callback);

    /**
     * This method updates or creates a new registration with the registration ID
     * specified.
     *
     * @param registration A registration object containing the description of the
     *                     registration to create or update. The registration ID
     *                     must be populated.
     * @return The updated registration containing the read-only parameters
     * (registration ID, ETag, and expiration time).
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    <T extends Registration> T upsertRegistration(T registration) throws NotificationHubsException;

    /**
     * This method updates or creates a new registration with the registration id
     * specified.
     *
     * @param registration A registration object containing the description of the
     *                     registration to create or update. The registration ID
     *                     must be populated.
     * @param callback     A callback, when invoked, returns the updated
     *                     registration containing the read-only parameters
     *                     (registration ID, ETag, and expiration time).
     */
    <T extends Registration> void upsertRegistrationAsync(T registration, final FutureCallback<T> callback);

    /**
     * Deletes a registration with the given registration containing a populated
     * registrationId.
     *
     * @param registration The registration containing the registrationId field
     *                     populated.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    void deleteRegistration(Registration registration) throws NotificationHubsException;

    /**
     * Deletes a registration with the given registration containing a populated
     * registrationId.
     *
     * @param registration The registration containing the registrationId field
     *                     populated.
     * @param callback     A callback when invoked returns nothing.
     */
    void deleteRegistrationAsync(Registration registration, FutureCallback<Object> callback);

    /**
     * Deletes a registration by the given registration ID.
     *
     * @param registrationId The registration ID for the registration to delete.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    void deleteRegistration(String registrationId) throws NotificationHubsException;

    /**
     * Deletes a registration by the given registration ID.
     *
     * @param registrationId The registration ID for the registration to delete.
     * @param callback       A callback when invoked returns nothing.
     */
    void deleteRegistrationAsync(String registrationId, FutureCallback<Object> callback);

    /**
     * Retrieves the description of a registration based on the ID.
     *
     * @param registrationId The ID for the registration to retrieve.
     * @return The registration with the ID matching the given registration ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     * @param <T> The type of Registration class.
     */
    <T extends Registration> T getRegistration(String registrationId) throws NotificationHubsException;

    /**
     * Retrieves the description of a registration based on the ID.
     *
     * @param registrationId The ID for the registration to retrieve.
     * @param callback       A callback, when invoked, returns the registration with
     *                       the ID matching the given registration ID.
     * @param <T> The type of Registration class.
     */
    <T extends Registration> void getRegistrationAsync(String registrationId, FutureCallback<T> callback);

    /**
     * Return all registrations in the current notification hub.
     *
     * @return Collection containing the registrations.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    CollectionResult getRegistrations() throws NotificationHubsException;

    /**
     * Return all registrations in the current notification hub.
     *
     * @param callback The callback when invoked, returns a collection containing
     *                 registrations.
     */
    void getRegistrationsAsync(FutureCallback<CollectionResult> callback);

    /**
     * Returns all registrations in this hub
     *
     * @param top               The maximum number of registrations to return (max
     *                          100)
     * @param continuationToken If not-null, continues iterating through a
     *                          previously requested query.
     * @return A collection containing the registrations.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    CollectionResult getRegistrations(int top, String continuationToken) throws NotificationHubsException;

    /**
     * Returns all registrations in this hub
     *
     * @param top               The maximum number of registrations to return (max
     *                          100)
     * @param continuationToken If not-null, continues iterating through a
     *                          previously requested query.
     * @param callback          A callback when invoked returns a collection
     *                          containing the registrations.
     */
    void getRegistrationsAsync(int top, String continuationToken, FutureCallback<CollectionResult> callback);

    /**
     * Returns all registrations with a specific tag
     *
     * @param tag The tag to search for registrations.
     * @return A collection of registrations with the given tag.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    CollectionResult getRegistrationsByTag(String tag) throws NotificationHubsException;

    /**
     * Returns all registrations with a specific tag
     *
     * @param tag      The tag to search for registrations.
     * @param callback A callback, when invoked, returns a collection of
     *                 registrations with the given tag.
     */
    void getRegistrationsByTagAsync(String tag, FutureCallback<CollectionResult> callback);

    /**
     * Returns all registrations with a specific tag
     *
     * @param tag               The tag to search for registrations.
     * @param top               The maximum number of registrations to return (max
     *                          100)
     * @param continuationToken If not-null, continues iterating through a
     *                          previously requested query.
     * @return A collection of registrations with the given tag.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    CollectionResult getRegistrationsByTag(String tag, int top, String continuationToken)
        throws NotificationHubsException;

    /**
     * Returns all registrations with a specific tag
     *
     * @param tag               The tag to search for registrations.
     * @param top               The maximum number of registrations to return (max
     *                          100)
     * @param continuationToken If not-null, continues iterating through a
     *                          previously requested query.
     * @param callback          A callback when invoked, returns a collection of
     *                          registrations with the given tag.
     */
    void getRegistrationsByTagAsync(String tag, int top, String continuationToken,
                                    FutureCallback<CollectionResult> callback);

    /**
     * Returns all registration with a specific channel (e.g. ChannelURI, device
     * token)
     *
     * @param channel The channel URI, device token or other unique PNS identifier.
     * @return A collection of registrations with matching channels.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    CollectionResult getRegistrationsByChannel(String channel) throws NotificationHubsException;

    /**
     * Returns all registration with a specific channel (e.g. ChannelURI, device
     * token)
     *
     * @param channel  The channel URI, device token or other unique PNS identifier.
     * @param callback A callback, when invoked, returns a collection of
     *                 registrations with matching channels.
     */
    void getRegistrationsByChannelAsync(String channel, FutureCallback<CollectionResult> callback);

    /**
     * Returns all registration with a specific channel (e.g. ChannelURI, device
     * token)
     *
     * @param channel           The channel URI, device token or other unique PNS
     *                          identifier.
     * @param top               The maximum number of registrations to return (max
     *                          100)
     * @param continuationToken If not-null, continues iterating through a
     *                          previously requested query.
     * @return A collection of registrations with matching channels.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    CollectionResult getRegistrationsByChannel(String channel, int top, String continuationToken)
        throws NotificationHubsException;

    /**
     * Returns all registration with a specific channel (e.g. ChannelURI, device
     * token)
     *
     * @param channel           The channel URI, device token or other unique PNS
     *                          identifier.
     * @param top               The maximum number of registrations to return (max
     *                          100)
     * @param continuationToken If not-null, continues iterating through a
     *                          previously requested query.
     * @param callback          A callback, when invoked, returns a collection of
     *                          registrations with matching channels.
     */
    void getRegistrationsByChannelAsync(String channel, int top, String continuationToken,
                                        FutureCallback<CollectionResult> callback);

    /**
     * Sends a notification to all eligible registrations (i.e. only correct
     * platform, if notification is platform specific)
     *
     * @param notification The notification to send to all eligible registrations.
     * @return A notification outcome with the tracking ID and notification ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    NotificationOutcome sendNotification(Notification notification) throws NotificationHubsException;

    /**
     * Sends a notification to all eligible registrations (i.e. only correct
     * platform, if notification is platform specific)
     *
     * @param notification The notification to send to all eligible registrations.
     * @param callback     A callback, when invoked, returns a notification outcome
     *                     with the tracking ID and notification ID.
     */
    void sendNotificationAsync(Notification notification, FutureCallback<NotificationOutcome> callback);

    /**
     * Sends a notifications to all eligible registrations with at least one of the
     * specified tags
     *
     * @param notification The notification to send to the audience with the
     *                     specified tags.
     * @param tags         The tags for targeting the notifications.
     * @return A notification outcome with the tracking ID and notification ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @SuppressWarnings("UnusedReturnValue")
    NotificationOutcome sendNotification(Notification notification, Set<String> tags) throws NotificationHubsException;

    /**
     * Sends a notifications to all eligible registrations with at least one of the
     * specified tags
     *
     * @param notification The notification to send to the audience with the
     *                     specified tags.
     * @param tags         The tags for targeting the notifications.
     * @param callback     A callback, when invoked, returns a notification outcome
     *                     with the tracking ID and notification ID.
     */
    void sendNotificationAsync(Notification notification, Set<String> tags,
                               FutureCallback<NotificationOutcome> callback);

    /**
     * Sends a notifications to all eligible registrations that satisfy the provided
     * tag expression.
     *
     * @param notification  The notification to send to the audience that matches
     *                      the specified tag expression.
     * @param tagExpression The tag expression for targeting the notifications.
     * @return A notification outcome with the tracking ID and notification ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @SuppressWarnings("UnusedReturnValue")
    NotificationOutcome sendNotification(Notification notification, String tagExpression)
        throws NotificationHubsException;

    /**
     * Sends a notifications to all eligible registrations that satisfy the provided
     * tag expression.
     *
     * @param notification  The notification to send to the audience that matches
     *                      the specified tag expression.
     * @param tagExpression The tag expression for targeting the notifications.
     * @param callback      A callback, when invoked, returns a notification outcome
     *                      with the tracking ID and notification ID.
     */
    void sendNotificationAsync(Notification notification, String tagExpression,
                               FutureCallback<NotificationOutcome> callback);

    /**
     * Schedules a notification at the given scheduled time.
     *
     * @param notification  The notification to send at the scheduled time.
     * @param scheduledTime The scheduled time for the notification.
     * @return A notification outcome with the tracking ID and notification ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    NotificationOutcome scheduleNotification(Notification notification, Date scheduledTime)
        throws NotificationHubsException;

    /**
     * Schedules a notification at the given scheduled time.
     *
     * @param notification  The notification to send at the scheduled time.
     * @param scheduledTime The scheduled time for the notification.
     * @param callback      A callback, when invoked, returns a notification outcome
     *                      with the tracking ID and notification ID.
     */
    void scheduleNotificationAsync(
        Notification notification,
        Date scheduledTime,
        FutureCallback<NotificationOutcome> callback);

    /**
     * Schedules a notification at the given time with a set of tags.
     *
     * @param notification  The notification to send at the given time.
     * @param tags          The tags associated with the notification targeting.
     * @param scheduledTime The scheduled time for the notification.
     * @return A notification outcome with the tracking ID and notification ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @SuppressWarnings("UnusedReturnValue")
    NotificationOutcome scheduleNotification(Notification notification, Set<String> tags, Date scheduledTime)
        throws NotificationHubsException;

    /**
     * Schedules a notification at the given time with a set of tags.  Note that this is not available on the free SKU.
     *
     * @param notification  The notification to send at the given time.
     * @param tags          The tags associated with the notification targeting.
     * @param scheduledTime The scheduled time for the notification.
     * @param callback      A callback, when invoked, returns a notification outcome
     *                      with the tracking ID and notification ID.
     */
    void scheduleNotificationAsync(
        Notification notification,
        Set<String> tags,
        Date scheduledTime,
        final FutureCallback<NotificationOutcome> callback
    );

    /**
     * Schedules a notification at the given time with a tag expression.  Note that this is not available on the free SKU.
     *
     * @param notification  The notification to send at the given time.
     * @param tagExpression The tag expression associated with the notification
     *                      targeting.
     * @param scheduledTime The scheduled time for the notification.
     * @return A notification outcome with the tracking ID and notification ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    @SuppressWarnings("UnusedReturnValue")
    NotificationOutcome scheduleNotification(Notification notification, String tagExpression, Date scheduledTime)
        throws NotificationHubsException;

    /**
     * Schedules a notification at the given time with a tag expression.  Note that this is not available on the free SKU.
     *
     * @param notification  The notification to send at the given time.
     * @param tagExpression The tag expression associated with the notification
     *                      targeting.
     * @param scheduledTime The scheduled time for the notification.
     * @param callback      A callback, when invoked, returns a notification outcome
     *                      with the tracking ID and notification ID.
     */
    void scheduleNotificationAsync(
        Notification notification,
        String tagExpression,
        Date scheduledTime,
        FutureCallback<NotificationOutcome> callback
    );

    /**
     * Sends a direct notification to a given device handle.
     *
     * @param notification The notification to send directly to the device handle.
     * @param deviceHandle The device handle to target for the notification.
     * @return A notification outcome with the tracking ID and notification ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    NotificationOutcome sendDirectNotification(Notification notification, String deviceHandle)
        throws NotificationHubsException;

    /**
     * Sends a direct notification to a given device handle.
     *
     * @param notification The notification to send directly to the device handle.
     * @param deviceHandle The device handle to target for the notification.
     * @param callback     A callback, when invoked, returns a notification outcome
     *                     with the tracking ID and notification ID.
     */
    void sendDirectNotificationAsync(
        Notification notification,
        String deviceHandle,
        FutureCallback<NotificationOutcome> callback
    );

    /**
     * Sends a direct notification to the given device handles.
     *
     * @param notification  The notification to send directly to the device handles.
     * @param deviceHandles The device handles to target for the notification.
     * @return A notification outcome with the tracking ID and notification ID.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    NotificationOutcome sendDirectNotification(
        Notification notification,
        List<String> deviceHandles
    ) throws NotificationHubsException;

    /**
     * Sends a direct notification to the given device handles.
     *
     * @param notification  The notification to send directly to the device handles.
     * @param deviceHandles The device handles to target for the notification.
     * @param callback      A callback, when invoked, returns a notification outcome
     *                      with the tracking ID and notification ID.
     */
    void sendDirectNotificationAsync(
        Notification notification,
        List<String> deviceHandles,
        FutureCallback<NotificationOutcome> callback
    );

    /**
     * Cancels the scheduled notification with the given notification ID.
     *
     * @param notificationId The notification ID of the notification to cancel.
     * @throws NotificationHubsException Thrown if there is a client error.
     */
    void cancelScheduledNotification(String notificationId) throws NotificationHubsException;

    /**
     * Cancels the scheduled notification with the given notification ID.
     *
     * @param notificationId The notification ID of the notification to cancel.
     * @param callback       A callback, when invoked, returns nothing.
     */
    void cancelScheduledNotificationAsync(String notificationId, FutureCallback<Object> callback);
}
